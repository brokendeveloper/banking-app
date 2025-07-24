package com.brokendev.backend.auth.integration;



import com.brokendev.backend.auth.dto.login.LoginRequestDTO;
import com.brokendev.backend.auth.dto.login.LoginResponseDTO;
import com.brokendev.backend.auth.dto.register.RegisterRequestDTO;
import com.brokendev.backend.auth.dto.register.RegisterResponseDTO;

import com.brokendev.backend.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticação", description = "Endpoints para autenticação e registro do usuário")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "login de usuário", description = "Autentica um usuário e retorna o login + JWT")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO request){
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "registro de usuário", description = "registra um novo usuário no sistema")
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO>register(@RequestBody @Valid RegisterRequestDTO request){
        return ResponseEntity.ok(authService.register(request));
    }
}