package com.brokendev.backend.notification.controller;

import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.common.exceptions.dto.ErrorResponseDTO;
import com.brokendev.backend.notification.dto.NotificationResponseDTO;
import com.brokendev.backend.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notification", description = "Endpoints for managing user notifications.")
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(
            summary = "List user notifications",
            description = "Retrieves a list of all notifications for the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of notifications successfully retrieved.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotificationResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User account not found.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> listNotifications(
            @AuthenticationPrincipal User user) {

        // Envolvendo o retorno no padrão ResponseEntity
        return ResponseEntity.ok(notificationService.listNotifications(user));
    }

    @Operation(
            summary = "Mark notification as read",
            description = "Marks the specified notification as read for the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Notification successfully marked as read. No content returned."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Notification not found or does not belong to the user.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        notificationService.markAsRead(id, user);

        return ResponseEntity.noContent().build();
    }
}