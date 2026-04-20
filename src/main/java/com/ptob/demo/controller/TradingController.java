package com.ptob.demo.controller;

import com.ptob.demo.model.OrderRequest;
import com.ptob.demo.service.TradingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trading")
public class TradingController {
    private final TradingService tradingService;
    public TradingController(TradingService tradingService) { this.tradingService = tradingService; }

    @PostMapping("/orders")
    public Map<String, Object> placeOrder(@Valid @RequestBody OrderRequest request) { return tradingService.placeOrder(request); }

    @DeleteMapping("/orders/{symbol}/{orderId}")
    public Map<String, Object> cancelOrder(@PathVariable String symbol, @PathVariable String orderId) {
        return tradingService.cancelOrder(symbol.replace('-', '/'), orderId);
    }

    @GetMapping("/books/{symbol}")
    public Map<String, Object> getBook(@PathVariable String symbol) { return tradingService.getBook(symbol.replace('-', '/')); }

    @GetMapping("/trades")
    public List<?> getTrades() { return tradingService.getTrades(); }
}
