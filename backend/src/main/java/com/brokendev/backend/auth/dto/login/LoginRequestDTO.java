package com.brokendev.backend.auth.dto.login;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @Email(message = "Invalid email")
        @NotBlank(message = "Field 'email' is required")
        @Schema(description = "User's email address", example = "john.doe@example.com")
        String email,

        @NotBlank(message = "Field 'password' is required")
        @Schema(description = "User's password for authentication", example = "strongPassword123")
        String password
) {
}
