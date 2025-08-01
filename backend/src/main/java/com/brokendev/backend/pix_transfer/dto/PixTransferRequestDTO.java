package com.brokendev.backend.pix_transfer.dto;

import com.brokendev.backend.enums.PixKeyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PixTransferRequestDTO(
        @NotNull(message = "The PIX key is required")
        @Schema(description = "The PIX key for the transfer", example = "john.doe@example.com")
        String pixKey,

        @NotNull(message = "The PIX key type is required")
        @Schema(description = "The type of the PIX key (e.g., EMAIL, CPF, PHONE)", example = "EMAIL", implementation = PixKeyType.class)
        PixKeyType pixKeyType,

        @NotNull(message = "The amount is required")
        @DecimalMin(value = "0.01", message = "The amount must be greater than zero")
        @Schema(description = "The amount to be transferred", example = "100.50")
        BigDecimal amount
) {
}