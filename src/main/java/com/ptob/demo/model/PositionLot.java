package com.ptob.demo.model;
import java.math.BigDecimal;
public record PositionLot(String asset, BigDecimal quantity, BigDecimal totalCost, BigDecimal weightedAverageCost) {}
