package com.ptob.demo.controller;

import com.ptob.demo.service.ReconciliationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ReconciliationController {
    private final ReconciliationService reconciliationService;
    public ReconciliationController(ReconciliationService reconciliationService) { this.reconciliationService = reconciliationService; }

    @GetMapping("/api/reconciliation/report")
    public Object report(@RequestParam String omnibusAccountId, @RequestParam Map<String, String> params) {
        Map<String, BigDecimal> walletBalances = new HashMap<>();
        params.forEach((key, value) -> {
            if (key.startsWith("wallet_")) walletBalances.put(key.substring("wallet_".length()).toUpperCase(), new BigDecimal(value));
        });
        return reconciliationService.reconcile(omnibusAccountId, walletBalances);
    }
}
