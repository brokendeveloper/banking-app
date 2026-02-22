package com.brokendev.backend.investment.dto;

import com.brokendev.backend.enums.InvestmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "Data required to create a new investment")
public record InvestmentRequestDTO(

        @Schema(description = "Type of the investment", example = "CDB", implementation = InvestmentType.class)
        @NotNull(message = "Investment type is required")
        InvestmentType type,

        @Schema(description = "Amount to be invested", example = "500.00")
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount
) {
}