package com.ptob.demo.model;
import java.time.Instant;
import java.util.List;
public record ReconciliationReport(Instant generatedAt, String omnibusAccountId, List<ReconciliationBreak> breaks) {}
