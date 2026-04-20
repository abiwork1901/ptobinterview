package com.ptob.demo.service;

import com.ptob.demo.model.BalanceSnapshot;
import com.ptob.demo.model.BalanceView;
import com.ptob.demo.model.LedgerEntry;
import com.ptob.demo.persistence.AccountBalanceEntity;
import com.ptob.demo.persistence.AccountBalanceRepository;
import com.ptob.demo.persistence.LedgerEntryEntity;
import com.ptob.demo.persistence.LedgerEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LedgerService {
    private final AccountBalanceRepository balanceRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final EventPublisher eventPublisher;

    public LedgerService(AccountBalanceRepository balanceRepository,
                         LedgerEntryRepository ledgerEntryRepository,
                         EventPublisher eventPublisher) {
        this.balanceRepository = balanceRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void fund(String accountId, String asset, BigDecimal amount) {
        AccountBalanceEntity balance = balance(accountId, asset);
        balance.setAvailable(balance.getAvailable().add(amount));
        balanceRepository.save(balance);
        post("external:" + asset, accountId, asset, amount, "fund-" + System.nanoTime(), "External funding");
    }

    @Transactional
    public boolean reserve(String accountId, String asset, BigDecimal amount) {
        AccountBalanceEntity balance = balance(accountId, asset);
        if (balance.getAvailable().compareTo(amount) < 0) return false;
        balance.setAvailable(balance.getAvailable().subtract(amount));
        balance.setReserved(balance.getReserved().add(amount));
        balanceRepository.save(balance);
        return true;
    }

    @Transactional
    public void release(String accountId, String asset, BigDecimal amount) {
        AccountBalanceEntity balance = balance(accountId, asset);
        if (balance.getReserved().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Reserved balance too low to release");
        }
        balance.setReserved(balance.getReserved().subtract(amount));
        balance.setAvailable(balance.getAvailable().add(amount));
        balanceRepository.save(balance);
    }

    @Transactional
    public void settleTrade(String buyerAccountId, String sellerAccountId, String baseAsset, String quoteAsset,
                            BigDecimal quantity, BigDecimal quoteAmount, String reference) {
        AccountBalanceEntity buyerQuote = balance(buyerAccountId, quoteAsset);
        AccountBalanceEntity sellerBase = balance(sellerAccountId, baseAsset);
        if (buyerQuote.getReserved().compareTo(quoteAmount) < 0) throw new IllegalStateException("Buyer reserved quote balance insufficient");
        if (sellerBase.getReserved().compareTo(quantity) < 0) throw new IllegalStateException("Seller reserved base balance insufficient");

        buyerQuote.setReserved(buyerQuote.getReserved().subtract(quoteAmount));
        sellerBase.setReserved(sellerBase.getReserved().subtract(quantity));
        balanceRepository.save(buyerQuote);
        balanceRepository.save(sellerBase);

        AccountBalanceEntity buyerBase = balance(buyerAccountId, baseAsset);
        buyerBase.setAvailable(buyerBase.getAvailable().add(quantity));
        AccountBalanceEntity sellerQuote = balance(sellerAccountId, quoteAsset);
        sellerQuote.setAvailable(sellerQuote.getAvailable().add(quoteAmount));
        balanceRepository.save(buyerBase);
        balanceRepository.save(sellerQuote);

        post(buyerAccountId, sellerAccountId, quoteAsset, quoteAmount, reference, "Trade quote leg");
        post(sellerAccountId, buyerAccountId, baseAsset, quantity, reference, "Trade base leg");
    }

    @Transactional
    public void allocateFromOmnibus(String omnibusAccountId, String accountId, String asset, BigDecimal amount, String reference) {
        transfer(omnibusAccountId, accountId, asset, amount, reference, "Omnibus allocation");
    }

    @Transactional
    public void transfer(String sourceAccountId, String destinationAccountId, String asset, BigDecimal amount, String reference, String description) {
        AccountBalanceEntity source = balance(sourceAccountId, asset);
        if (source.getAvailable().compareTo(amount) < 0) throw new IllegalArgumentException("Insufficient source balance");
        source.setAvailable(source.getAvailable().subtract(amount));
        AccountBalanceEntity destination = balance(destinationAccountId, asset);
        destination.setAvailable(destination.getAvailable().add(amount));
        balanceRepository.save(source);
        balanceRepository.save(destination);
        post(sourceAccountId, destinationAccountId, asset, amount, reference, description);
    }

    @Transactional(readOnly = true)
    public BalanceView getBalance(String accountId, String asset) {
        AccountBalanceEntity b = balance(accountId, asset);
        return new BalanceView(accountId, asset, b.getAvailable(), b.getReserved());
    }

    @Transactional(readOnly = true)
    public BalanceSnapshot getBalanceSnapshot(String accountId) {
        List<AccountBalanceEntity> rows = balanceRepository.findByAccountId(accountId);
        Map<String, BigDecimal> available = new LinkedHashMap<>();
        Map<String, BigDecimal> reserved = new LinkedHashMap<>();
        rows.forEach(r -> { available.put(r.getAsset(), r.getAvailable()); reserved.put(r.getAsset(), r.getReserved()); });
        return new BalanceSnapshot(accountId, available, reserved);
    }

    @Transactional(readOnly = true)
    public List<LedgerEntry> getJournal() {
        return ledgerEntryRepository.findAll().stream()
                .sorted(Comparator.comparing(LedgerEntryEntity::getCreatedAt))
                .map(e -> new LedgerEntry(e.getEntryId(), e.getDebitAccount(), e.getCreditAccount(), e.getAsset(), e.getAmount(), e.getReference(), e.getDescription(), e.getCreatedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public BigDecimal available(String accountId, String asset) {
        return balance(accountId, asset).getAvailable();
    }

    @Transactional(readOnly = true)
    public BigDecimal totalAvailableExcluding(String excludedAccountId, String asset) {
        return balanceRepository.findByAsset(asset).stream()
                .filter(row -> !excludedAccountId.equals(row.getAccountId()))
                .map(AccountBalanceEntity::getAvailable)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public Set<String> allAssets() {
        return balanceRepository.findAll().stream().map(AccountBalanceEntity::getAsset).collect(Collectors.toSet());
    }

    private AccountBalanceEntity balance(String accountId, String asset) {
        return balanceRepository.findByAccountIdAndAsset(accountId, asset)
                .orElseGet(() -> balanceRepository.save(new AccountBalanceEntity(accountId, asset)));
    }

    private void post(String debit, String credit, String asset, BigDecimal amount, String reference, String description) {
        String entryId = UUID.randomUUID().toString();
        LedgerEntryEntity entity = new LedgerEntryEntity(entryId, debit, credit, asset, amount, reference, description, Instant.now());
        ledgerEntryRepository.save(entity);
        eventPublisher.publish("ledger.entries", reference, Map.of(
                "entryId", entryId,
                "debitAccount", debit,
                "creditAccount", credit,
                "asset", asset,
                "amount", amount,
                "reference", reference,
                "description", description));
    }
}
