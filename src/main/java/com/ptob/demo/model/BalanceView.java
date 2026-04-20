package com.ptob.demo.model;
import java.math.BigDecimal;
public record BalanceView(String accountId, String asset, BigDecimal available, BigDecimal reserved) {}
