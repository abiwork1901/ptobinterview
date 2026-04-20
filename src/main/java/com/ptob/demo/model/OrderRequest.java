package com.ptob.demo.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OrderRequest(
        @NotBlank String accountId,
        @NotBlank String symbol,
        @NotNull Side side,
        @NotNull @DecimalMin(value = "0.00000001") BigDecimal quantity,
        @NotNull @DecimalMin(value = "0.00000001") BigDecimal price,
        @NotBlank String idempotencyKey
) {}
