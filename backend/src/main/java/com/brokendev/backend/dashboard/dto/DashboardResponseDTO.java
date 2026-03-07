package com.brokendev.backend.dashboard.dto;

import com.brokendev.backend.account.dto.TransactionStatementResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Aggregated data for the user's dashboard home screen")
public record DashboardResponseDTO(

        @Schema(description = "User's full name", example = "John Doe")
        String name,

        @Schema(description = "User's email address", example = "john.doe@example.com")
        String email,

        @Schema(description = "Current account balance", example = "1500.50")
        BigDecimal balance,

        @Schema(description = "List of the user's most recent transactions")
        List<TransactionStatementResponseDTO> lastTransactions
) {
}