package com.brokendev.backend.common.exceptions.dto;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        int status,
        String error,
        String message,
        LocalDateTime timestamp
) {
}
