package com.brokendev.backend.investment.dto;

import com.brokendev.backend.enums.InvestmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Details of a performed investment")
public record InvestmentResponseDTO(

        @Schema(description = "Unique identifier of the investment", example = "1")
        Long id,

        @Schema(description = "Type of the investment", example = "FIXED_INCOME")
        InvestmentType type,

        @Schema(description = "Invested amount", example = "1000.00")
        BigDecimal amount,

        @Schema(description = "Date when the investment was made")
        LocalDateTime investmentDate,

        @Schema(description = "Calculated expected return after maturity", example = "1120.50")
        BigDecimal expectedReturn,

        @Schema(description = "Maturity date when the investment can be redeemed")
        LocalDateTime maturityDate,

        @Schema(description = "Indicates if the investment has already been redeemed", example = "false")
        boolean redeemed
) {
}