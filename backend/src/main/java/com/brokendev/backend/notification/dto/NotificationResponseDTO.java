package com.brokendev.backend.notification.dto;

import java.time.LocalDateTime;

public record NotificationResponseDTO(
        Long id,
        String title,
        String message,
        LocalDateTime createdAt,
        boolean read
) {
}
