package com.ptob.demo.model;
import java.math.BigDecimal;
public record ReconciliationBreak(String asset, BigDecimal walletBalance, BigDecimal internalOmnibusBalance, BigDecimal clientAllocatedBalance, BigDecimal unexplainedDifference) {}
