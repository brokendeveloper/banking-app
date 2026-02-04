package com.brokendev.backend.card.dto;

import java.time.LocalDate;

public record CardResponseDTO(
        Long id,
        String cardNumber,
        String holderName,
        String expiration,
        boolean blocked,
        LocalDate createdAt
) {
}
