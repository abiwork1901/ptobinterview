package com.ptob.demo.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequest(
        @NotBlank String sourceAccountId,
        @NotBlank String destinationAccountId,
        @NotBlank String asset,
        @NotNull @DecimalMin(value = "0.00000001") BigDecimal amount,
        @NotBlank String idempotencyKey
) {}
