package com.ptob.demo.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "positions", uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "asset"}))
public class PositionEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "account_id", nullable = false)
    private String accountId;
    @Column(nullable = false)
    private String asset;
    @Column(nullable = false, precision = 38, scale = 8)
    private BigDecimal quantity = BigDecimal.ZERO;
    @Column(name = "total_cost", nullable = false, precision = 38, scale = 8)
    private BigDecimal totalCost = BigDecimal.ZERO;
    @Version
    private Long version;

    public PositionEntity() {}
    public PositionEntity(String accountId, String asset) { this.accountId = accountId; this.asset = asset; }
    public String getAccountId() { return accountId; }
    public String getAsset() { return asset; }
    public BigDecimal getQuantity() { return quantity; }
    public BigDecimal getTotalCost() { return totalCost; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
}
