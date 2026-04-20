package com.ptob.demo.service;

import com.ptob.demo.model.AllocationInstruction;
import com.ptob.demo.model.AllocationRequest;
import com.ptob.demo.model.ProportionalAllocationItem;
import com.ptob.demo.model.ProportionalAllocationRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OmnibusServiceTest {

    private final LedgerService ledgerService = mock(LedgerService.class);
    private final EventPublisher eventPublisher = mock(EventPublisher.class);
    private final IdempotencyService idempotencyService = mock(IdempotencyService.class);

    private final OmnibusService omnibusService =
            new OmnibusService(ledgerService, eventPublisher, idempotencyService);

    @Test
    void proportionalAllocateSplitsAndPreservesTotal() {
        when(idempotencyService.lookup("prop-1")).thenReturn(Optional.empty());

        ProportionalAllocationRequest request = new ProportionalAllocationRequest(
                "omnibus-usdt",
                "USDT",
                new BigDecimal("100"),
                List.of(
                        new ProportionalAllocationItem("client-a", new BigDecimal("1")),
                        new ProportionalAllocationItem("client-b", new BigDecimal("3"))
                ),
                "prop-1"
        );

        Map<String, Object> result = omnibusService.proportionalAllocate(request);

        verify(ledgerService).allocateFromOmnibus(
                eq("omnibus-usdt"), eq("client-a"), eq("USDT"), eq(new BigDecimal("25.00000000")), any()
        );
        verify(ledgerService).allocateFromOmnibus(
                eq("omnibus-usdt"), eq("client-b"), eq("USDT"), eq(new BigDecimal("75.00000000")), any()
        );
        verify(idempotencyService).record(eq("prop-1"), any());
        verify(eventPublisher).publish(eq("omnibus.allocations"), any(), eq(request));

        assertEquals("ALLOCATED", result.get("status"));
        assertEquals(new BigDecimal("100"), result.get("totalAmount"));
    }

    @Test
    void allocateReturnsDuplicateWithoutReapplyingLedger() {
        when(idempotencyService.lookup("dup-1")).thenReturn(Optional.of("alloc-direct-existing"));

        AllocationRequest request = new AllocationRequest(
                "omnibus-usdt",
                "USDT",
                List.of(new AllocationInstruction("client-a", new BigDecimal("12.5"))),
                "dup-1"
        );

        Map<String, Object> result = omnibusService.allocate(request);

        assertEquals("DUPLICATE", result.get("status"));
        assertEquals("alloc-direct-existing", result.get("reference"));
        verify(ledgerService, never()).allocateFromOmnibus(any(), any(), any(), any(), any());
        verify(eventPublisher, never()).publish(any(), any(), any());
    }
}
