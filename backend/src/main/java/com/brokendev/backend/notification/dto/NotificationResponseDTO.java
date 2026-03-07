package com.brokendev.backend.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Details of a user notification")
public record NotificationResponseDTO(

        @Schema(description = "Unique identifier of the notification", example = "1")
        Long id,

        @Schema(description = "Title of the notification", example = "PIX Received")
        String title,

        @Schema(description = "Detailed message of the notification", example = "You received a PIX transfer of 150.00 from sender@example.com")
        String message,

        @Schema(description = "Date and time when the notification was created")
        LocalDateTime createdAt,

        @Schema(description = "Indicates whether the notification has been read by the user", example = "false")
        boolean read
) {
}