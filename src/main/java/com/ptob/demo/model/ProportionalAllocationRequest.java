package com.ptob.demo.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record ProportionalAllocationRequest(
        @NotBlank String omnibusAccountId,
        @NotBlank String asset,
        @NotNull @DecimalMin(value = "0.00000001") BigDecimal totalAmount,
        @NotEmpty List<@Valid ProportionalAllocationItem> beneficiaries,
        @NotBlank String idempotencyKey
) {}
