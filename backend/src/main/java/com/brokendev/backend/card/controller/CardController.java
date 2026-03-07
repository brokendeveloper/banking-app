package com.brokendev.backend.card.controller;

import com.brokendev.backend.account.dto.AccountBalanceResponseDTO;
import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.card.dto.CardBlockResponseDTO;
import com.brokendev.backend.card.dto.CardCreateRequestDTO;
import com.brokendev.backend.card.dto.CardResponseDTO;
import com.brokendev.backend.card.service.CardService;
import com.brokendev.backend.common.exceptions.dto.ErrorResponseDTO;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Tag(name = "Card Management", description = "Endpoints for managing user cards and your operations.")
public class CardController {

    private final CardService cardService;

    @Operation(
            summary = "Create card",
            description = "Generate a new card for a for an authenticated user. The titular name can be informed on request body."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful created card.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CardResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found for the authenticated user.",
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
    @PostMapping
    public ResponseEntity<CardResponseDTO> createCard(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid CardCreateRequestDTO request) {

        CardResponseDTO responseDTO = cardService.createCard(user.getEmail(), request);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDTO.id())
                .toUri();
        return ResponseEntity.created(uri).body(responseDTO);
    }

    @Operation(
            summary = "List cards",
            description = "Return all cards linked of user authenticated account"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful list of cards.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CardResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cards not found for the authenticated user.",
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
    @GetMapping
    public ResponseEntity<List<CardResponseDTO>> listCards(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(cardService.listCards(user.getEmail()));
    }

    @Operation(
            summary = "Block card",
            description = "Blocks card for the ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful blocked card.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CardBlockResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Card not found for the authenticated user.",
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
    @PatchMapping("/{id}/block")
    public ResponseEntity<CardBlockResponseDTO> blockCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.blockCard(id));
    }

    @Operation(
            summary = "Unlock card",
            description = "Unlock card for the ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful unblocked card.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CardResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Card not found for the authenticated user.",
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
    @PatchMapping("/{id}/unblock")
    public ResponseEntity<CardBlockResponseDTO> unblockCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.unblockCard(id));
    }
}
