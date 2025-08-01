package com.brokendev.backend.boleto_payment.controller;

import com.brokendev.backend.boleto_payment.dto.BoletoPaymentRequestDTO;
import com.brokendev.backend.boleto_payment.dto.BoletoPaymentResponseDTO;
import com.brokendev.backend.boleto_payment.service.BoletoPaymentService;
import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.common.exceptions.dto.ErrorResponseDTO;
import com.brokendev.backend.common.exceptions.dto.ValidationErrorResponseDTO;
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
@RequestMapping("/api/boleto")
@RequiredArgsConstructor
@Tag(name = "Boleto Payment", description = "Endpoints for managing boleto payments.")
public class BoletoPaymentController {

    private final BoletoPaymentService boletoPaymentService;

    @Operation(summary = "Pay a boleto", description = "Performs a boleto payment as specified by the user.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Boleto payment successfully performed.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BoletoPaymentResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data (e.g., invalid barcode or amount).",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found for the authenticated user.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict: Insufficient balance to perform the payment.",
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
    @PostMapping("/pay")
    public ResponseEntity<BoletoPaymentResponseDTO> payBoleto(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid BoletoPaymentRequestDTO request) {
        BoletoPaymentResponseDTO response = boletoPaymentService.payBoleto(user.getEmail(), request);
        return ResponseEntity.ok(response);
    }
}