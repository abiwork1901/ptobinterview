package com.ptob.demo.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "trades")
public class TradeEntity {
    @Id
    private String tradeId;
    private String symbol;
    private BigDecimal price;
    private BigDecimal quantity;
    private String buyOrderId;
    private String sellOrderId;
    private Instant executedAt;

    public TradeEntity() {}
    public TradeEntity(String tradeId, String symbol, BigDecimal price, BigDecimal quantity, String buyOrderId, String sellOrderId, Instant executedAt) {
        this.tradeId = tradeId; this.symbol = symbol; this.price = price; this.quantity = quantity; this.buyOrderId = buyOrderId; this.sellOrderId = sellOrderId; this.executedAt = executedAt;
    }
    public String getTradeId() { return tradeId; }
    public String getSymbol() { return symbol; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getQuantity() { return quantity; }
    public String getBuyOrderId() { return buyOrderId; }
    public String getSellOrderId() { return sellOrderId; }
    public Instant getExecutedAt() { return executedAt; }
}
