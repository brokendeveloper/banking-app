package com.brokendev.backend.dashboard.service;

import com.brokendev.backend.account.dto.TransactionStatementResponseDTO;
import com.brokendev.backend.account.service.AccountService;
import com.brokendev.backend.dashboard.dto.DashboardResponseDTO;
import com.brokendev.backend.common.exceptions.UserAccountNotFoundException;
import com.brokendev.backend.account.domain.AccountRepository;
import com.brokendev.backend.common.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;

    private final AccountRepository accountRepository;

    private final AccountService accountService;

    public DashboardResponseDTO getDashboard(String userEmail){
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("usuário não encontrado"));

        var account = accountRepository.findByUser(user)
                .orElseThrow(() -> new UserAccountNotFoundException("conta não encontrada"));

        List<TransactionStatementResponseDTO> lastTransactions = accountService.getAccountStatement(userEmail)
                .stream()
                .limit(5)
                .toList();

        return new DashboardResponseDTO(
                user.getName(),
                user.getEmail(),
                account.getBalance(),
                lastTransactions
        );
    }
}
