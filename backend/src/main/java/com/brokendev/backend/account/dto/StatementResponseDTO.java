package com.brokendev.backend.account.dto;

import java.util.List;

public record StatementResponseDTO(
        List<TransactionStatementResponseDTO> transactions
) {
}
