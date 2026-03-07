package com.brokendev.backend.profile.dto;

public record UserProfileUpdateResponseDTO(
        String name,
        String email,
        String telephone
) {
}
