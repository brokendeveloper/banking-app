package com.brokendev.backend.profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data required to update the user's profile information")
public record UserProfileUpdateDTO(

        @Schema(description = "New full name (optional)", example = "John Smith")
        String name,

        @Schema(description = "New email address (optional)", example = "john.smith@newemail.com")
        String email,

        @Schema(description = "New telephone number (optional)", example = "+55 11 98888-8888")
        String telephone
) {
}