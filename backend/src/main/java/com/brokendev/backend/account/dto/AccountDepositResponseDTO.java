package com.brokendev.backend.account.dto;

import java.math.BigDecimal;

public record AccountDepositResponseDTO(
        BigDecimal newBalance,
        String message
) {
}
