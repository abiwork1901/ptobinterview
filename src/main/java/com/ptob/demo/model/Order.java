package com.ptob.demo.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Order {
    private final String orderId = UUID.randomUUID().toString();
    private final String accountId;
    private final String symbol;
    private final Side side;
    private final BigDecimal price;
    private final BigDecimal originalQuantity;
    private BigDecimal remainingQuantity;
    private OrderStatus status;
    private final Instant createdAt = Instant.now();
    private final String idempotencyKey;

    public Order(String accountId, String symbol, Side side, BigDecimal quantity, BigDecimal price, String idempotencyKey) {
        this.accountId = accountId;
        this.symbol = symbol;
        this.side = side;
        this.originalQuantity = quantity;
        this.remainingQuantity = quantity;
        this.price = price;
        this.status = OrderStatus.ACCEPTED;
        this.idempotencyKey = idempotencyKey;
    }

    public String getOrderId() { return orderId; }
    public String getAccountId() { return accountId; }
    public String getSymbol() { return symbol; }
    public Side getSide() { return side; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getOriginalQuantity() { return originalQuantity; }
    public BigDecimal getRemainingQuantity() { return remainingQuantity; }
    public OrderStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public void fill(BigDecimal qty) {
        remainingQuantity = remainingQuantity.subtract(qty);
        if (remainingQuantity.signum() == 0) status = OrderStatus.FILLED;
        else status = OrderStatus.PARTIALLY_FILLED;
    }
    public void reject() { this.status = OrderStatus.REJECTED; }
    public void cancel() { this.status = OrderStatus.CANCELLED; }
}
