package com.brokendev.backend.investment.controller;

import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.common.exceptions.dto.ErrorResponseDTO;
import com.brokendev.backend.common.exceptions.dto.ValidationErrorResponseDTO;
import com.brokendev.backend.investment.dto.InvestmentRequestDTO;
import com.brokendev.backend.investment.dto.InvestmentResponseDTO;
import com.brokendev.backend.investment.service.InvestmentService;
import com.brokendev.backend.pix_transfer.dto.PixTransferResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/investments")
@RequiredArgsConstructor
@Tag(name = "Investiment", description = "Endpoints for managing investments.")
public class InvestmentController {

    private final InvestmentService investmentService;

    @Operation(
            summary = "Invest an amount",
            description = "Allows the authenticated user to invest a specific amount from their balance."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Investment successfully created.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InvestmentResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data or insufficient balance.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PostMapping
    public ResponseEntity<InvestmentResponseDTO> invest(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid InvestmentRequestDTO request) {

        InvestmentResponseDTO response = investmentService.invest(user.getEmail(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "List all investments",
            description = "Retrieves a list of all investments made by the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of investments successfully retrieved.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InvestmentResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User account not found.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<InvestmentResponseDTO>> listInvestments(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(investmentService.listInvestments(user.getEmail()));
    }

    @Operation(
            summary = "Redeem investment",
            description = "Allows the user to redeem a matured investment, returning the amount plus interests to their balance."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Investment successfully redeemed.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InvestmentResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Investment not eligible for redemption (e.g., not matured yet).",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Investment not found.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PostMapping("/{id}/redeem")
    public ResponseEntity<InvestmentResponseDTO> redeemInvestment(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return ResponseEntity.ok(investmentService.redeemInvestment(user.getEmail(), id));
    }

}
