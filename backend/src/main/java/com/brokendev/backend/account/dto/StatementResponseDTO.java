package com.brokendev.backend.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record StatementResponseDTO(
        @Schema(description = "List of all transactions for the account")
        List<TransactionStatementResponseDTO> transactions
) {
}