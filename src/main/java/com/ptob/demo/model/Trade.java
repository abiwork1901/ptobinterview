package com.ptob.demo.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Trade(
        String tradeId,
        String symbol,
        BigDecimal price,
        BigDecimal quantity,
        String buyOrderId,
        String sellOrderId,
        Instant executedAt
) {
    public static Trade of(String symbol, BigDecimal price, BigDecimal quantity, String buyOrderId, String sellOrderId) {
        return new Trade(UUID.randomUUID().toString(), symbol, price, quantity, buyOrderId, sellOrderId, Instant.now());
    }
}
