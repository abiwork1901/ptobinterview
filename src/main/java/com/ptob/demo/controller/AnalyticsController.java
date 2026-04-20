package com.ptob.demo.controller;

import com.ptob.demo.service.CostBasisService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalyticsController {
    private final CostBasisService costBasisService;
    public AnalyticsController(CostBasisService costBasisService) { this.costBasisService = costBasisService; }

    @GetMapping("/api/positions/{accountId}/{asset}")
    public Object getPosition(@PathVariable String accountId, @PathVariable String asset) { return costBasisService.getPosition(accountId, asset); }
}
