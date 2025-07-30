package com.brokendev.backend.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public record AccountDepositResponseDTO(
        @Schema(description = "The updated balance of the account after the deposit", example = "1750.75")
        BigDecimal balance,
        @Schema(description = "A message confirming the deposit status", example = "Deposit made successfully!")
        String message
) {
}