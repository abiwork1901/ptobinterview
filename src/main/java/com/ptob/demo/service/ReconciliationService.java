package com.ptob.demo.service;

import com.ptob.demo.model.ReconciliationBreak;
import com.ptob.demo.model.ReconciliationReport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ReconciliationService {
    private final LedgerService ledgerService;

    public ReconciliationService(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @Transactional(readOnly = true)
    public ReconciliationReport reconcile(String omnibusAccountId, Map<String, BigDecimal> walletBalances) {
        Set<String> assets = ledgerService.allAssets();
        List<ReconciliationBreak> breaks = new ArrayList<>();
        for (String asset : assets) {
            BigDecimal wallet = walletBalances.getOrDefault(asset, BigDecimal.ZERO);
            BigDecimal omnibus = ledgerService.available(omnibusAccountId, asset);
            BigDecimal clients = ledgerService.totalAvailableExcluding(omnibusAccountId, asset);
            BigDecimal difference = wallet.subtract(omnibus.add(clients));
            breaks.add(new ReconciliationBreak(asset, wallet, omnibus, clients, difference));
        }
        return new ReconciliationReport(Instant.now(), omnibusAccountId, breaks);
    }
}
