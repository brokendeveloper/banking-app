package com.brokendev.backend.pix_transfer.dto;

import com.brokendev.backend.enums.PixKeyType;
import com.brokendev.backend.enums.PixTransactionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PixTransferResponseDTO(
        @Schema(description = "Email of the user who initiated the transfer", example = "sender@example.com")
        String senderEmail,
        @Schema(description = "Email of the user who received the transfer", example = "receiver@example.com")
        String receiverEmail,
        @Schema(description = "The amount of the transfer", example = "100.50")
        BigDecimal amount,
        @Schema(description = "Date and time when the transfer was completed", example = "2024-07-31T16:45:00")
        LocalDateTime timestamp,
        @Schema(description = "The status of the PIX transaction", example = "COMPLETED", implementation = PixTransactionStatus.class)
        PixTransactionStatus status,
        @Schema(description = "A description of the transaction status", example = "PIX transfer successfully performed!")
        String description,
        @Schema(description = "The type of the PIX key used for the transfer", example = "EMAIL", implementation = PixKeyType.class)
        PixKeyType pixKeyType,
        @Schema(description = "The PIX key used for the transfer", example = "john.doe@example.com")
        String pixKey
) {
}