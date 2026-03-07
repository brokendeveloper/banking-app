package com.brokendev.backend.profile.dto;

import com.brokendev.backend.account.dto.AccountInfoResponseDTO; // Import atualizado
import com.brokendev.backend.card.dto.CardResponseDTO; // Import atualizado
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Detailed profile information of the authenticated user")
public record UserProfileResponseDTO(

        @Schema(description = "User's full name", example = "John Doe")
        String name,

        @Schema(description = "User's email address", example = "john.doe@example.com")
        String email,

        @Schema(description = "User's CPF (Brazilian ID)", example = "123.456.789-00")
        String cpf,

        @Schema(description = "User's contact telephone number", example = "+55 11 99999-9999")
        String telephone,

        @Schema(description = "Basic account information associated with the user")
        AccountInfoResponseDTO account,

        @Schema(description = "List of cards belonging to the user's account")
        List<CardResponseDTO> cards
) {
}