package com.brokendev.backend.card.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CardBlockResponseDTO(

        @Schema(description = "Id of created card", example = "133")
        Long id,

        @Schema(description = "Information about status of card - Can be blocked or unblocked", example = "Blocked")
        boolean blocked,

        @Schema(description = "Message about the situation of card", example = "The card was successfully blocked")
        String message
) {
}
