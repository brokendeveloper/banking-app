package com.brokendev.backend.common.exceptions.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorResponseDTO(
        int status,
        String error,
        String message,
        Map<String, String> fieldErrors,
        LocalDateTime timestamp
) {
}
