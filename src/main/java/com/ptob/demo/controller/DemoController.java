package com.ptob.demo.controller;

import com.ptob.demo.service.CostBasisService;
import com.ptob.demo.service.LedgerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
public class DemoController {
    private final LedgerService ledgerService;
    private final CostBasisService costBasisService;

    public DemoController(LedgerService ledgerService, CostBasisService costBasisService) {
        this.ledgerService = ledgerService;
        this.costBasisService = costBasisService;
    }

    @PostMapping("/api/demo/bootstrap")
    public Map<String, Object> bootstrap() {
        ledgerService.fund("omnibus-main", "USDT", new BigDecimal("1000000"));
        ledgerService.fund("trader-a", "USDT", new BigDecimal("500000"));
        ledgerService.fund("trader-b", "BTC", new BigDecimal("25"));
        ledgerService.fund("trader-c", "USDT", new BigDecimal("250000"));
        costBasisService.recordBuy("trader-b", "BTC", new BigDecimal("25"), new BigDecimal("1125000"));
        return Map.of("status", "BOOTSTRAPPED");
    }
}
