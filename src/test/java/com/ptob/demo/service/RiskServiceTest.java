package com.ptob.demo.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RiskServiceTest {

    private final RiskService riskService = new RiskService();

    @Test
    void allowsOrderWithinConfiguredLimit() {
        assertDoesNotThrow(() ->
                riskService.validateOrder("trader-a", new BigDecimal("999999.99"))
        );
    }

    @Test
    void rejectsOrderAboveDefaultLimitForUnknownAccount() {
        assertThrows(IllegalArgumentException.class, () ->
                riskService.validateOrder("unknown-account", new BigDecimal("300000"))
        );
    }
}
