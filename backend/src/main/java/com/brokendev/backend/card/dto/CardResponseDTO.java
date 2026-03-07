package com.brokendev.backend.card.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CardResponseDTO(

        @Schema(description = "Id of created card", example = "133")
        Long id,

        @Schema(description = "The number of card", example = "1234 5678 9999 9999")
        String cardNumber,

        @Schema(description = "The name of owner of this card", example = "Joseph F. Laurent")
        String holderName,

        @Schema(description = "Expiration or date limit of card", example = "99/99/99")
        String expiration,

        @Schema(description = "Information about status of card - Can be blocked or unblocked", example = "Blocked")
        boolean blocked,

        @Schema(description = "Date about creation of card", example = "99/99/99 at 99:00:00")
        LocalDate createdAt
) {
}
