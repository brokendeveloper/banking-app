package com.brokendev.backend.pix_transfer.controller;

import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.common.exceptions.dto.ErrorResponseDTO;
import com.brokendev.backend.common.exceptions.dto.ValidationErrorResponseDTO;
import com.brokendev.backend.pix_transfer.dto.PixTransferRequestDTO;
import com.brokendev.backend.pix_transfer.dto.PixTransferResponseDTO;
import com.brokendev.backend.pix_transfer.service.PixTransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pix")
@RequiredArgsConstructor
@Tag(name = "PIX Transfer", description = "Endpoints for managing PIX transfers.")
public class PixTransferController {

    private final PixTransferService pixTransferService;

    @Operation(summary = "PIX transfer", description = "Performs a PIX transfer to another account.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "PIX transfer successfully performed.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PixTransferResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data (e.g., invalid PIX key type or amount).",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sender or receiver account not found.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict: Insufficient balance or not allowed to transfer to self.",
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
    @PostMapping("/transfer")
    public ResponseEntity<PixTransferResponseDTO> transferPix(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid PixTransferRequestDTO request) {
        PixTransferResponseDTO response = pixTransferService.transferPix(user.getEmail(), request);
        return ResponseEntity.ok(response);
    }
}