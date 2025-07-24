package com.brokendev.backend.auth.dto.register;

import io.swagger.v3.oas.annotations.media.Schema;

public record RegisterResponseDTO(
        @Schema(description = "The name of the successfully registered user.", example = "John Doe")
        String name,

        @Schema(description = "The email of the successfully registered user.", example = "john.doe@example.com")
        String email,

        @Schema(description = "A confirmation message for the user.", example = "User registered successfully")
        String message
) {
}
