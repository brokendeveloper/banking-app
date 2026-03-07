package com.brokendev.backend.profile.controller;

import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.common.exceptions.dto.ErrorResponseDTO;
import com.brokendev.backend.profile.dto.UserProfileResponseDTO;
import com.brokendev.backend.profile.dto.UserProfileUpdateDTO;
import com.brokendev.backend.profile.dto.UserProfileUpdateResponseDTO;
import com.brokendev.backend.profile.service.ProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Profile", description = "Endpoints for managing the authenticated user's profile and access.")
@RestController
@RequestMapping("/api/profile") // Rota base alterada para refletir a Feature
public class UserProfileController {

    private final ProfileService profileService;

    public UserProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Operation(
            summary = "Verify access",
            description = "Verifies if the user has a valid token and authorized access."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Access granted."
            )
    })
    @GetMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyAccess() {
        return ResponseEntity.ok(Map.of("message", "success"));
    }

    @Operation(
            summary = "Get user profile",
            description = "Retrieves the authenticated user's data, including account and active cards."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile information successfully retrieved.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserProfileResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User or account not found.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @GetMapping
    public ResponseEntity<UserProfileResponseDTO> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(profileService.getProfile(user));
    }

    @Operation(
            summary = "Update user profile",
            description = "Updates the authenticated user's profile information (e.g., name, email, telephone)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile successfully updated.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserProfileUpdateResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data (e.g., malformed email).",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PutMapping
    public ResponseEntity<UserProfileUpdateResponseDTO> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid UserProfileUpdateDTO dto
    ) {
        UserProfileUpdateResponseDTO response = profileService.updateProfile(user.getId(), dto);
        return ResponseEntity.ok(response);
    }
}