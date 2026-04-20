package com.ptob.demo.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "idempotency_records")
public class IdempotencyRecordEntity {
    @Id
    private String idempotencyKey;
    private String referenceValue;
    private Instant createdAt;

    public IdempotencyRecordEntity() {}
    public IdempotencyRecordEntity(String idempotencyKey, String referenceValue, Instant createdAt) {
        this.idempotencyKey = idempotencyKey; this.referenceValue = referenceValue; this.createdAt = createdAt;
    }
    public String getIdempotencyKey() { return idempotencyKey; }
    public String getReferenceValue() { return referenceValue; }
    public Instant getCreatedAt() { return createdAt; }
}
