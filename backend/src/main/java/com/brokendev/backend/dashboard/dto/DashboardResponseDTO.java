package com.brokendev.backend.dashboard.dto;

import com.brokendev.backend.account.dto.TransactionStatementResponseDTO;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponseDTO(
        String name,
        String email,
        BigDecimal balance,
        List<TransactionStatementResponseDTO> lastTransactions
) {
}
