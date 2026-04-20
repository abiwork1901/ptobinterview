package com.ptob.demo.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AllocationInstruction(@NotBlank String accountId,
                                    @NotNull @DecimalMin(value = "0.00000001") BigDecimal amount) {}
