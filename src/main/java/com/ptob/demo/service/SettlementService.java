package com.ptob.demo.service;

import com.ptob.demo.model.TransferRequest;
import com.ptob.demo.persistence.TransferEntity;
import com.ptob.demo.persistence.TransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class SettlementService {
    private final LedgerService ledgerService;
    private final EventPublisher eventPublisher;
    private final IdempotencyService idempotencyService;
    private final TransferRepository transferRepository;

    public SettlementService(LedgerService ledgerService, EventPublisher eventPublisher, IdempotencyService idempotencyService, TransferRepository transferRepository) {
        this.ledgerService = ledgerService;
        this.eventPublisher = eventPublisher;
        this.idempotencyService = idempotencyService;
        this.transferRepository = transferRepository;
    }

    @Transactional
    public Map<String, Object> internalTransfer(TransferRequest request) {
        String existing = idempotencyService.lookup(request.idempotencyKey()).orElse(null);
        if (existing != null) return Map.of("status", "DUPLICATE", "reference", existing);

        String reference = "transfer-" + System.nanoTime();
        ledgerService.transfer(request.sourceAccountId(), request.destinationAccountId(), request.asset(), request.amount(), reference, "Internal transfer");
        transferRepository.save(new TransferEntity(reference, request.sourceAccountId(), request.destinationAccountId(), request.asset(), request.amount(), Instant.now()));
        idempotencyService.record(request.idempotencyKey(), reference);
        eventPublisher.publish("settlement.transfers", reference, request);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "SETTLED");
        response.put("reference", reference);
        response.put("sourceAccountId", request.sourceAccountId());
        response.put("destinationAccountId", request.destinationAccountId());
        response.put("asset", request.asset());
        response.put("amount", request.amount());
        return response;
    }
}
