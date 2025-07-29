package com.brokendev.backend.boleto_payment.controller;

import com.brokendev.backend.boleto_payment.dto.BoletoPaymentRequestDTO;
import com.brokendev.backend.boleto_payment.dto.BoletoPaymentResponseDTO;
import com.brokendev.backend.boleto_payment.service.BoletoPaymentService;
import com.brokendev.backend.common.domain.user.User;
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
@RequestMapping("/api/boleto")
@RequiredArgsConstructor
public class BoletoPaymentController {

    private final BoletoPaymentService boletoPaymentService;

    @Operation(summary = "pagamento de boleto", description = "realiza o pagamento de boleto informado pelo usuário")
    @PostMapping("/pay")
    public ResponseEntity<BoletoPaymentResponseDTO> payBoleto(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid BoletoPaymentRequestDTO request) {
        BoletoPaymentResponseDTO response = boletoPaymentService.payBoleto(user.getEmail(), request);
        return ResponseEntity.ok(response);
    }
}
