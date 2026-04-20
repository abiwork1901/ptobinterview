package com.ptob.demo.controller;

import com.ptob.demo.model.FundRequest;
import com.ptob.demo.model.TransferRequest;
import com.ptob.demo.service.LedgerService;
import com.ptob.demo.service.SettlementService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ledger")
public class LedgerController {
    private final LedgerService ledgerService;
    private final SettlementService settlementService;

    public LedgerController(LedgerService ledgerService, SettlementService settlementService) {
        this.ledgerService = ledgerService;
        this.settlementService = settlementService;
    }

    @PostMapping("/fund")
    public Map<String, Object> fund(@Valid @RequestBody FundRequest request) {
        ledgerService.fund(request.accountId(), request.asset(), request.amount());
        return Map.of("status", "FUNDED");
    }

    @PostMapping("/transfer")
    public Map<String, Object> transfer(@Valid @RequestBody TransferRequest request) {
        return settlementService.internalTransfer(request);
    }

    @GetMapping("/balances/{accountId}/{asset}")
    public Object getBalance(@PathVariable String accountId, @PathVariable String asset) { return ledgerService.getBalance(accountId, asset); }

    @GetMapping("/balances/{accountId}")
    public Object getBalanceSnapshot(@PathVariable String accountId) { return ledgerService.getBalanceSnapshot(accountId); }

    @GetMapping("/journal")
    public List<?> journal() { return ledgerService.getJournal(); }
}
