package com.ptob.demo.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "ledger_entries", indexes = {
        @Index(name = "idx_ledger_reference", columnList = "reference"),
        @Index(name = "idx_ledger_asset", columnList = "asset")
})
public class LedgerEntryEntity {
    @Id
    private String entryId;
    @Column(name = "debit_account", nullable = false)
    private String debitAccount;
    @Column(name = "credit_account", nullable = false)
    private String creditAccount;
    @Column(nullable = false)
    private String asset;
    @Column(nullable = false, precision = 38, scale = 8)
    private BigDecimal amount;
    @Column(nullable = false)
    private String reference;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private Instant createdAt;

    public LedgerEntryEntity() {}
    public LedgerEntryEntity(String entryId, String debitAccount, String creditAccount, String asset, BigDecimal amount, String reference, String description, Instant createdAt) {
        this.entryId = entryId; this.debitAccount = debitAccount; this.creditAccount = creditAccount; this.asset = asset;
        this.amount = amount; this.reference = reference; this.description = description; this.createdAt = createdAt;
    }
    public String getEntryId() { return entryId; }
    public String getDebitAccount() { return debitAccount; }
    public String getCreditAccount() { return creditAccount; }
    public String getAsset() { return asset; }
    public BigDecimal getAmount() { return amount; }
    public String getReference() { return reference; }
    public String getDescription() { return description; }
    public Instant getCreatedAt() { return createdAt; }
}
