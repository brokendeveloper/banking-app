package com.brokendev.backend.boleto_payment.dto;

import com.brokendev.backend.common.enums.BoletoPaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BoletoPaymentResponseDTO(
        @Schema(description = "The barcode of the paid boleto", example = "12345678901234567890123456789012345678901234")
        String barcode,
        @Schema(description = "The amount paid", example = "500.25")
        BigDecimal amount,
        @Schema(description = "The date and time the payment was made", example = "2024-07-31T15:30:00")
        LocalDateTime paymentDate,
        @Schema(description = "The status of the boleto payment", example = "PAID", implementation = BoletoPaymentStatus.class)
        BoletoPaymentStatus status,
        @Schema(description = "A description of the payment status", example = "Boleto payment successfully performed!")
        String description
) {
}