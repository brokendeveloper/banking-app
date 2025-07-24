package com.brokendev.backend.auth.dto.login;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponseDTO(
        @Schema(description = "A confirmation message for the user.", example = "User registered successfully")
        String name,

        @Schema(description = "The email of the authenticated user.", example = "john.doe@example.com")
        String email,

        @Schema(description = "The JWT token for authentication.",
                example = "JWT valid token")
        String token

) {
}
