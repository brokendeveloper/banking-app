package com.brokendev.backend.auth.dto.register;

public record RegisterResponseDTO(
        String name,
        String email,
        String message
) {
}
