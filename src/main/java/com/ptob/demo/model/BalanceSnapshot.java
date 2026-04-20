package com.ptob.demo.model;
import java.math.BigDecimal;
import java.util.Map;
public record BalanceSnapshot(String accountId, Map<String, BigDecimal> available, Map<String, BigDecimal> reserved) {}
