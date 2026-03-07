package com.brokendev.backend.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public record AccountBalanceResponseDTO(
        @Schema(description = "The current balance of the account", example = "1500.75")
        BigDecimal balance
) {
}