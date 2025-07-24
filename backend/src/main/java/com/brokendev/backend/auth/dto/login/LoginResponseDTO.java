package com.brokendev.backend.auth.dto.login;

public record LoginResponseDTO(
        String name,
        String email,
        String token

) {
}
