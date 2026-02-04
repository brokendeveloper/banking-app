package com.brokendev.backend.card.dto;

public record CardBlockResponseDTO(
        Long id,
        boolean blocked,
        String message
) {
}
