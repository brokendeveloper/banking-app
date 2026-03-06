package com.brokendev.backend.dashboard.controller;


import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.dashboard.dto.DashboardResponseDTO;
import com.brokendev.backend.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(
            summary = "Dashboard do usuário",
            description = "Retorna informações resumidas para a tela inicial do app."
    )
    @GetMapping
    public ResponseEntity<DashboardResponseDTO> getDashboard(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(dashboardService.getDashboard(user.getEmail()));
    }
}
