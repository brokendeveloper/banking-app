package com.brokendev.backend.account.controller;

import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.account.dto.AccountBalanceResponseDTO;
import com.brokendev.backend.account.dto.AccountDepositRequestDTO;
import com.brokendev.backend.account.dto.AccountDepositResponseDTO;
import com.brokendev.backend.account.dto.TransactionStatementResponseDTO;
import com.brokendev.backend.account.service.AccountService;
import com.brokendev.backend.common.exceptions.dto.ErrorResponseDTO;
import com.brokendev.backend.common.exceptions.dto.ValidationErrorResponseDTO; // Adicionado para 400 Bad Request
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@Tag(name = "Account Management", description = "Endpoints for managing user accounts and financial operations.")
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "Consult account balance", description = "Retrieves the balance of the authenticated user's account.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful retrieval of account balance.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountBalanceResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found for the authenticated user.",
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
    @GetMapping("/balance")
    public ResponseEntity<AccountBalanceResponseDTO> getAccountBalance(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(accountService.getAccountBalance(user.getEmail()));
    }


    @Operation(summary = "Deposit any amount", description = "Performs a deposit of a chosen amount into the user's account.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Deposit performed successfully, returns updated balance.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountDepositResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data (e.g., negative amount, empty fields).",
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
                    responseCode = "500",
                    description = "Internal server error.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PostMapping("/deposit")
    public ResponseEntity<AccountDepositResponseDTO> performDeposit(@AuthenticationPrincipal User user, @RequestBody @Valid AccountDepositRequestDTO requestDTO) {
        AccountDepositResponseDTO responseDTO = accountService.performDeposit(user.getEmail(), requestDTO.amount());
        return ResponseEntity.ok(responseDTO);
    }


    @Operation(
            summary = "Get account statement",
            description = "Returns the transaction history/statement for the authenticated user's account."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved account statement.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionStatementResponseDTO.class)) // Pode ser List<TransactionStatementResponseDTO> no schema
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found for the authenticated user.",
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
    @GetMapping("/statement")
    public ResponseEntity<List<TransactionStatementResponseDTO>> getAccountStatement(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(accountService.getAccountStatement(user.getEmail()));
    }
}