package com.brokendev.backend.profile.dto;

public record UserProfileUpdateDTO(
        String name,
        String email,
        String telephone
) {
}
