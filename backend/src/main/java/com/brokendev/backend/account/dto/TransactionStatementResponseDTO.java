package com.brokendev.backend.account.dto;

import com.brokendev.backend.common.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionStatementResponseDTO(
        @Schema(description = "Type of the transaction", example = "PIX_RECEIVED", implementation = TransactionType.class)
        TransactionType type,
        @Schema(description = "Amount of the transaction", example = "100.00")
        BigDecimal amount,
        @Schema(description = "Date and time when the transaction occurred", example = "2024-07-30T10:30:00")
        LocalDateTime date,
        @Schema(description = "Description of the transaction", example = "PIX recebido de joao@exemplo.com")
        String description
) {
}