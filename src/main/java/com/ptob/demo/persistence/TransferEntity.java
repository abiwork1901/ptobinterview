package com.ptob.demo.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transfers")
public class TransferEntity {
    @Id
    private String reference;
    private String sourceAccountId;
    private String destinationAccountId;
    private String asset;
    private BigDecimal amount;
    private Instant createdAt;

    public TransferEntity() {}
    public TransferEntity(String reference, String sourceAccountId, String destinationAccountId, String asset, BigDecimal amount, Instant createdAt) {
        this.reference = reference; this.sourceAccountId = sourceAccountId; this.destinationAccountId = destinationAccountId; this.asset = asset; this.amount = amount; this.createdAt = createdAt;
    }
    public String getReference() { return reference; }
    public String getSourceAccountId() { return sourceAccountId; }
    public String getDestinationAccountId() { return destinationAccountId; }
    public String getAsset() { return asset; }
    public BigDecimal getAmount() { return amount; }
    public Instant getCreatedAt() { return createdAt; }
}
