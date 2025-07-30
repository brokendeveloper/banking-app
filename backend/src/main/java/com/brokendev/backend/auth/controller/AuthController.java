package com.brokendev.backend.auth.controller;



import com.brokendev.backend.auth.dto.login.LoginRequestDTO;
import com.brokendev.backend.auth.dto.login.LoginResponseDTO;
import com.brokendev.backend.auth.dto.register.RegisterRequestDTO;
import com.brokendev.backend.auth.dto.register.RegisterResponseDTO;

import com.brokendev.backend.auth.service.AuthService;
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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for authentication and register of users")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "User login", description = "Authenticates a user and returns a JWT for access.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful login, returns JWT token and user data.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data (ex: email format, empty fields)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials (user not found or password incorrect).",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO request){
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "User register", description = "Register a new user on system")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successful registration, returns the registration confirmation.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegisterResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data (ex: email format, invalid CPF, empty fields).",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict: Email or CPF already exist.",
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
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO>register(@RequestBody @Valid RegisterRequestDTO request){
        RegisterResponseDTO responseDTO = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
}