package com.brokendev.backend.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public record AccountInfoResponseDTO(
        @Schema(description = "Unique identifier of the account", example = "12345")
        Long id,
        @Schema(description = "The current balance of the account", example = "1500.75")
        BigDecimal balance
) {
}