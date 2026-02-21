package com.brokendev.backend.card.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CardCreateRequestDTO(
        @NotBlank(message = "The holdername is required")
        @Schema(description = "The holdername of the card", example = "Joseph F. Laurent")
        String holderName
) { }
