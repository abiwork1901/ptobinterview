package com.ptob.demo.controller;

import com.ptob.demo.model.ReferenceData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReferenceController {
    @GetMapping("/api/reference/overview")
    public ReferenceData overview() {
        return new ReferenceData(
                "ptob-service-v4",
                "Reference implementation for trading, omnibus allocation, PostgreSQL-backed ledgering, Kafka eventing, and reconciliation",
                List.of(
                        "Price-time priority matching with per-symbol in-memory order books",
                        "Single-writer order-book mutation model",
                        "Pre-trade reservation for quote/base assets",
                        "Direct omnibus allocation and proportional omnibus allocation",
                        "Double-entry immutable journal persisted in PostgreSQL",
                        "Weighted-average cost basis tracking in PostgreSQL",
                        "Idempotent internal transfer settlement",
                        "Kafka producer/consumer event flow",
                        "Wallet-vs-ledger reconciliation"
                ),
                List.of(
                        "POST /api/demo/bootstrap",
                        "POST /api/ledger/fund",
                        "POST /api/trading/orders",
                        "DELETE /api/trading/orders/{symbol}/{orderId}",
                        "POST /api/omnibus/allocate",
                        "POST /api/omnibus/allocate/proportional",
                        "POST /api/ledger/transfer",
                        "GET /api/reconciliation/report",
                        "GET /api/positions/{accountId}/{asset}"
                )
        );
    }
}
