package com.brokendev.backend.boleto_payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record BoletoPaymentRequestDTO(
        @NotBlank(message = "The barcode is required")
        @Schema(description = "The barcode of the boleto to be paid", example = "12345678901234567890123456789012345678901234")
        String barcode,

        @NotNull(message = "The amount is required")
        @DecimalMin(value = "0.01", message = "The amount must be greater than zero")
        @Schema(description = "The amount of the boleto", example = "500.25")
        BigDecimal amount
) {
}