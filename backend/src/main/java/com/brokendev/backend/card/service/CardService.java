package com.brokendev.backend.card.service;


import com.brokendev.backend.account.domain.Account;
import com.brokendev.backend.card.domain.Card;
import com.brokendev.backend.card.dto.CardBlockResponseDTO;
import com.brokendev.backend.card.dto.CardCreateRequestDTO;
import com.brokendev.backend.card.dto.CardResponseDTO;
import com.brokendev.backend.common.exceptions.UserAccountNotFoundException;
import com.brokendev.backend.common.exceptions.CardNotFoundException;
import com.brokendev.backend.account.domain.AccountRepository;
import com.brokendev.backend.card.domain.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.brokendev.backend.card.utils.CardUtils.*;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;

    public CardService(CardRepository cardRepository, AccountRepository accountRepository){
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public CardResponseDTO createCard(String userEmail, CardCreateRequestDTO request) {
        Account account = accountRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new UserAccountNotFoundException("Account not found"));

        // Simple generate card
        String cardNumber = generateCardNumber();
        String expiration = generateExpiration();
        LocalDate createdAt = LocalDate.now();

        Card card = new Card();
        card.setAccount(account);
        card.setHolderName(request.holderName());
        card.setCardNumber(cardNumber);
        card.setExpiration(expiration);
        card.setBlocked(false);
        card.setCreatedAt(createdAt);

        cardRepository.save(card);

        return new CardResponseDTO(
                card.getId(),
                maskCardNumber(card.getCardNumber()),
                card.getHolderName(),
                card.getExpiration(),
                card.isBlocked(),
                card.getCreatedAt()
        );
    }

    public List<CardResponseDTO> listCards(String userEmail) {
        Account account = accountRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new UserAccountNotFoundException("Account not found"));
        return cardRepository.findByAccount(account)
                .stream()
                .map(card -> new CardResponseDTO(
                        card.getId(),
                        maskCardNumber(card.getCardNumber()),
                        card.getHolderName(),
                        card.getExpiration(),
                        card.isBlocked(),
                        card.getCreatedAt()
                ))
                .toList();
    }

    @Transactional
    public CardBlockResponseDTO blockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found"));
        card.setBlocked(true);
        cardRepository.save(card);
        return new CardBlockResponseDTO(card.getId(), card.isBlocked(), "Card succeeded blocked.");
    }

    @Transactional
    public CardBlockResponseDTO unblockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found"));
        card.setBlocked(false);
        cardRepository.save(card);
        return new CardBlockResponseDTO(card.getId(), card.isBlocked(), "Card succeeded unblocked.");
    }




}
