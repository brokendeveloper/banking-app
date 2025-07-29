package com.brokendev.backend.pix_transfer.controller;

import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.pix_transfer.dto.PixTransferRequestDTO;
import com.brokendev.backend.pix_transfer.dto.PixTransferResponseDTO;
import com.brokendev.backend.pix_transfer.service.PixTransferService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/pix")
@RequiredArgsConstructor
public class PixTransferController {

    private final PixTransferService pixTransferService;

    @Operation(summary = "transferência pix", description = "realiza a transferência pix para outra conta")
    @PostMapping("/transfer")
    public ResponseEntity<PixTransferResponseDTO> transferPix(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid PixTransferRequestDTO request) {
        PixTransferResponseDTO response = pixTransferService.transferPix(user.getEmail(), request);
        return ResponseEntity.ok(response);
    }
}
