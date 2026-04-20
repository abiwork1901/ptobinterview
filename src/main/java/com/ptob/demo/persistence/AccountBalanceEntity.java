package com.ptob.demo.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "account_balances", uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "asset"}))
public class AccountBalanceEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "account_id", nullable = false)
    private String accountId;
    @Column(nullable = false)
    private String asset;
    @Column(nullable = false, precision = 38, scale = 8)
    private BigDecimal available = BigDecimal.ZERO;
    @Column(nullable = false, precision = 38, scale = 8)
    private BigDecimal reserved = BigDecimal.ZERO;
    @Version
    private Long version;

    public AccountBalanceEntity() {}
    public AccountBalanceEntity(String accountId, String asset) {
        this.accountId = accountId; this.asset = asset;
    }
    public Long getId() { return id; }
    public String getAccountId() { return accountId; }
    public String getAsset() { return asset; }
    public BigDecimal getAvailable() { return available; }
    public BigDecimal getReserved() { return reserved; }
    public void setAvailable(BigDecimal available) { this.available = available; }
    public void setReserved(BigDecimal reserved) { this.reserved = reserved; }
}
