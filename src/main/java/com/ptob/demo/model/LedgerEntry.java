package com.ptob.demo.model;

import java.math.BigDecimal;
import java.time.Instant;

public record LedgerEntry(
        String entryId,
        String debitAccount,
        String creditAccount,
        String asset,
        BigDecimal amount,
        String reference,
        String description,
        Instant createdAt
) {}
