package com.brokendev.backend.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record AccountDepositRequestDTO(
        @NotNull(message = "Field 'amount' is required")
        @DecimalMin(value = "0.01", message = "Deposit amount must be greater than zero")
        @Schema(description = "The amount to be deposited into the account", example = "250.00")
        BigDecimal amount
) {
}