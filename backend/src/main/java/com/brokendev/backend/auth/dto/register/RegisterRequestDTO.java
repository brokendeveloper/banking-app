package com.brokendev.backend.auth.dto.register;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
        @NotBlank(message = "field 'name' is required ")
        @Schema(description = "User's full name", example = "John Doe")
        String name,

        @Pattern(regexp = "\\d{11}", message = "CPF must contain 11 numbers")
        @NotBlank(message = "Field 'CPF' is required")
        @Schema(description = "User's CPF number (11 digits)", example = "12345678901")
        String cpf,

        @Email(message = "Invalid email")
        @NotBlank(message = "Field 'email' is required")
        @Schema(description = "User's email address", example = "john.doe@example.com")
        String email,


        @NotBlank(message = "Field 'password' is required")
        @Size(min= 6, message = "Password must contain at least 6 characters")
        @Schema(description = "User's password (minimum 6 characters)", example = "strongPassword123")
        String password,

        @Pattern(regexp = "\\d{10,11}", message = "Field 'telephone' must contain at least 10 or 11 digits")
        @NotBlank(message = "Field 'telephone' is required")
        @Schema(description = "User's telephone number (10 or 11 digits)", example = "81912345678")
        String telephone
) {
}
