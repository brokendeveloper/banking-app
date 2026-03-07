package com.brokendev.backend.profile.dto;

import com.brokendev.backend.account.dto.AccountInfoResponseDTO;
import com.brokendev.backend.card.dto.CardResponseDTO;

import java.util.List;

public record UserProfileResponseDTO(
        String name,
        String email,
        String cpf,
        String telephone,
        AccountInfoResponseDTO account,
        List<CardResponseDTO> cards
) {
}
