package com.ptob.demo.service;

import com.ptob.demo.model.AllocationInstruction;
import com.ptob.demo.model.AllocationRequest;
import com.ptob.demo.model.ProportionalAllocationItem;
import com.ptob.demo.model.ProportionalAllocationRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class OmnibusService {
    private final LedgerService ledgerService;
    private final EventPublisher eventPublisher;
    private final IdempotencyService idempotencyService;

    public OmnibusService(LedgerService ledgerService, EventPublisher eventPublisher, IdempotencyService idempotencyService) {
        this.ledgerService = ledgerService;
        this.eventPublisher = eventPublisher;
        this.idempotencyService = idempotencyService;
    }

    @Transactional
    public Map<String, Object> allocate(AllocationRequest request) {
        String existing = idempotencyService.lookup(request.idempotencyKey()).orElse(null);
        if (existing != null) return Map.of("status", "DUPLICATE", "reference", existing);

        String reference = "alloc-direct-" + System.nanoTime();
        BigDecimal total = BigDecimal.ZERO;
        for (AllocationInstruction allocation : request.allocations()) {
            ledgerService.allocateFromOmnibus(request.omnibusAccountId(), allocation.accountId(), request.asset(), allocation.amount(), reference);
            total = total.add(allocation.amount());
        }
        idempotencyService.record(request.idempotencyKey(), reference);
        eventPublisher.publish("omnibus.allocations", reference, request);
        return Map.of("status", "ALLOCATED", "reference", reference, "asset", request.asset(), "totalAllocated", total, "allocationCount", request.allocations().size());
    }

    @Transactional
    public Map<String, Object> proportionalAllocate(ProportionalAllocationRequest request) {
        String existing = idempotencyService.lookup(request.idempotencyKey()).orElse(null);
        if (existing != null) return Map.of("status", "DUPLICATE", "reference", existing);

        BigDecimal totalWeight = request.beneficiaries().stream().map(ProportionalAllocationItem::weight).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalWeight.signum() <= 0) throw new IllegalArgumentException("Total weight must be positive");

        String reference = "alloc-prop-" + System.nanoTime();
        BigDecimal allocated = BigDecimal.ZERO;
        List<Map<String, Object>> rows = new ArrayList<>();
        for (int i = 0; i < request.beneficiaries().size(); i++) {
            ProportionalAllocationItem item = request.beneficiaries().get(i);
            BigDecimal share = (i == request.beneficiaries().size() - 1)
                    ? request.totalAmount().subtract(allocated)
                    : request.totalAmount().multiply(item.weight()).divide(totalWeight, 8, RoundingMode.DOWN);
            allocated = allocated.add(share);
            ledgerService.allocateFromOmnibus(request.omnibusAccountId(), item.accountId(), request.asset(), share, reference);
            rows.add(Map.of("accountId", item.accountId(), "weight", item.weight(), "allocated", share));
        }

        idempotencyService.record(request.idempotencyKey(), reference);
        eventPublisher.publish("omnibus.allocations", reference, request);
        return Map.of("status", "ALLOCATED", "reference", reference, "asset", request.asset(), "totalAmount", request.totalAmount(), "rows", rows);
    }
}
