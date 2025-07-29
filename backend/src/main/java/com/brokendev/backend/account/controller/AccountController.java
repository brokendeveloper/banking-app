package com.brokendev.backend.account.controller;


import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.account.dto.AccountBalanceResponseDTO;
import com.brokendev.backend.account.dto.AccountDepositRequestDTO;
import com.brokendev.backend.account.dto.AccountDepositResponseDTO;
import com.brokendev.backend.account.dto.TransactionStatementResponseDTO;
import com.brokendev.backend.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "consulta de saldo", description = "consulta o saldo do usuário")
    @GetMapping("/balance")
    public ResponseEntity<AccountBalanceResponseDTO>getBalance(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(accountService.getAccountBalance(user.getEmail()));

    }


    @Operation(summary = "depósito de valor qualquer", description = "realiza o depósito de um valor escolhido pelo usuário")
    @PostMapping("/deposit")
    public ResponseEntity<AccountDepositResponseDTO>deposit(@AuthenticationPrincipal User user, @RequestBody @Valid AccountDepositRequestDTO requestDTO) {
        AccountDepositResponseDTO responseDTO = accountService.performDeposit(user.getEmail(), requestDTO.amount());
        return ResponseEntity.ok(responseDTO);
    }


    @Operation(
            summary = "Extrato da conta",
            description = "Retorna o extrato/histórico de transações da conta do usuário autenticado."
    )
    @GetMapping("/statement")
    public ResponseEntity<List<TransactionStatementResponseDTO>> getStatement(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(accountService.getAccountStatement(user.getEmail()));
    }
}