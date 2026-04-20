package com.ptob.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptob.demo.model.OrderRequest;
import com.ptob.demo.model.Side;
import com.ptob.demo.service.CostBasisService;
import com.ptob.demo.service.LedgerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.kafka.enabled=false"
})
@AutoConfigureMockMvc
class TradingControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired LedgerService ledgerService;
    @Autowired CostBasisService costBasisService;

    @Test
    void placesOrder() throws Exception {
        ledgerService.fund("test-buyer", "USDT", new BigDecimal("100000"));
        ledgerService.fund("test-seller", "BTC", new BigDecimal("10"));
        costBasisService.recordBuy("test-seller", "BTC", new BigDecimal("10"), new BigDecimal("450000"));

        mockMvc.perform(post("/api/trading/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer local-dev-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new OrderRequest("test-seller", "BTC/USDT", Side.SELL, new BigDecimal("1"), new BigDecimal("45000"), "seller-1"))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/trading/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer local-dev-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new OrderRequest("test-buyer", "BTC/USDT", Side.BUY, new BigDecimal("1"), new BigDecimal("45000"), "buyer-1"))))
                .andExpect(status().isOk());
    }
}
