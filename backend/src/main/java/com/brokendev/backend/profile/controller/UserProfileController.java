package com.brokendev.backend.profile.controller;


import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.profile.dto.UserProfileResponseDTO;
import com.brokendev.backend.profile.dto.UserProfileUpdateDTO;
import com.brokendev.backend.profile.dto.UserProfileUpdateResponseDTO;
import com.brokendev.backend.profile.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Tag(name = "Auth-Info", description = "Endpoint para verificar se o usuário com token válido possui acesso")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Operation(summary = "Verificação", description = "verifica se o usuário tem acesso autorizado")
    @GetMapping
    public ResponseEntity<Map<String, String>> getUser() {
        return ResponseEntity.ok(Map.of("message", "success"));
    }

    @Operation(
            summary = "Perfil do usuário",
            description = "Retorna os dados do usuário autenticado, incluindo conta e cartões."
    )
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponseDTO> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userProfileService.getProfile(user));
    }

    @Operation(
            summary = "Atualização de perfil",
            description = "Atualiza os dados do perfil de um usuário autenticado."
    )
    @PutMapping("/profile")
    public ResponseEntity<UserProfileUpdateResponseDTO> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid UserProfileUpdateDTO dto
    ) {
        UserProfileUpdateResponseDTO response = userProfileService.updateProfile(user.getId(), dto);
        return ResponseEntity.ok(response);
    }
}