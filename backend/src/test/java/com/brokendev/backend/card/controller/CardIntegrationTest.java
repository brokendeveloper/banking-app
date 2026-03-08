package com.brokendev.backend.card.controller;

import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.common.domain.user.UserRepository;
import com.brokendev.backend.account.domain.Account;
import com.brokendev.backend.account.domain.AccountRepository;
import com.brokendev.backend.card.domain.Card;
import com.brokendev.backend.card.domain.CardRepository;
import com.brokendev.backend.card.dto.CardCreateRequestDTO;
import com.brokendev.backend.infra.security.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CardIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private TokenService tokenService;

    private String validToken;
    private User testUser;
    private Account testAccount;
    private Card testCard;

    @BeforeEach
    void setUp() {
        cardRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setName("Luccas Card Test");
        testUser.setEmail("luccas.card@email.com");
        testUser.setCpf("11122233344");
        testUser.setPassword("senhaSegura123");
        testUser.setTelephone("81977777777");
        userRepository.save(testUser);

        testAccount = new Account();
        testAccount.setUser(testUser);
        testAccount.setAccountNumber("11223-4");
        testAccount.setBalance(new BigDecimal("1000.00"));
        accountRepository.save(testAccount);

        testCard = new Card();
        testCard.setAccount(testAccount);
        testCard.setHolderName("Luccas C Test");
        testCard.setCardNumber("1234567890123456"); // Sem máscara no banco
        testCard.setExpiration("12/30");
        testCard.setBlocked(false);
        testCard.setCreatedAt(LocalDate.now());
        cardRepository.save(testCard);

        validToken = tokenService.generateToken(testUser);
    }

    @Test
    @Transactional
    void shouldCreateCardSuccessfully() throws Exception {
        CardCreateRequestDTO request = new CardCreateRequestDTO("Luccas Virtual");

        mockMvc.perform(post("/api/cards")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()) // Assumindo que seu controller retorna 201 Created
                .andExpect(jsonPath("$.holderName").value("Luccas Virtual"))
                .andExpect(jsonPath("$.cardNumber").exists())
                .andExpect(jsonPath("$.blocked").value(false));
    }

    @Test
    @Transactional
    void shouldListCardsSuccessfully() throws Exception {
        mockMvc.perform(get("/api/cards")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].holderName").value("Luccas C Test"));
    }

    @Test
    @Transactional
    void shouldBlockCardSuccessfully() throws Exception {
        mockMvc.perform(patch("/api/cards/" + testCard.getId() + "/block")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.blocked").value(true));
    }

    @Test
    @Transactional
    void shouldUnblockCardSuccessfully() throws Exception {
        testCard.setBlocked(true);
        cardRepository.save(testCard);

        mockMvc.perform(patch("/api/cards/" + testCard.getId() + "/unblock")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.blocked").value(false));
    }
}