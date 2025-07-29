package com.brokendev.backend.account.dto;

import java.math.BigDecimal;

public record AccountInfoResponseDTO(
        Long id,
        BigDecimal balance
) {
}
