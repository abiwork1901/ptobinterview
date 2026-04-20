package com.ptob.demo.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RiskService {
    private final Map<String, BigDecimal> maxOrderNotionalByAccount = new ConcurrentHashMap<>();

    public RiskService() {
        maxOrderNotionalByAccount.put("trader-a", new BigDecimal("1000000"));
        maxOrderNotionalByAccount.put("trader-b", new BigDecimal("1000000"));
        maxOrderNotionalByAccount.put("trader-c", new BigDecimal("1000000"));
    }

    public void validateOrder(String accountId, BigDecimal notional) {
        BigDecimal limit = maxOrderNotionalByAccount.getOrDefault(accountId, new BigDecimal("250000"));
        if (notional.compareTo(limit) > 0) {
            throw new IllegalArgumentException("Order breaches account risk limit. notional=" + notional + ", limit=" + limit);
        }
    }
}
