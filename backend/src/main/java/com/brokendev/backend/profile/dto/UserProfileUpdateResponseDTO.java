package com.brokendev.backend.profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing the updated profile information")
public record UserProfileUpdateResponseDTO(

        @Schema(description = "Updated full name", example = "John Smith")
        String name,

        @Schema(description = "Updated email address", example = "john.smith@newemail.com")
        String email,

        @Schema(description = "Updated telephone number", example = "+55 11 98888-8888")
        String telephone
) {
}