package com.brokendev.backend.profile.controller;

import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.common.domain.user.UserRepository;
import com.brokendev.backend.account.domain.Account;
import com.brokendev.backend.account.domain.AccountRepository;
import com.brokendev.backend.card.domain.Card;
import com.brokendev.backend.card.domain.CardRepository;
import com.brokendev.backend.profile.dto.UserProfileUpdateDTO;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserProfileIntegrationTest {

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

    @BeforeEach
    void setUp() {

        cardRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();


        testUser = new User();
        testUser.setName("Luccas Final");
        testUser.setEmail("luccas.final@email.com");
        testUser.setCpf("99999999999");
        testUser.setPassword("senhaSegura123");
        testUser.setTelephone("81999999999");
        userRepository.save(testUser);


        Account testAccount = new Account();
        testAccount.setUser(testUser);
        testAccount.setAccountNumber("9999-9");
        testAccount.setBalance(new BigDecimal("5000.00"));
        accountRepository.save(testAccount);


        Card testCard = new Card();
        testCard.setAccount(testAccount);
        testCard.setHolderName("Luccas Final");
        testCard.setCardNumber("1234567890123456");
        testCard.setExpiration("12/30");
        testCard.setBlocked(false);
        testCard.setCreatedAt(LocalDate.now());
        cardRepository.save(testCard);


        validToken = tokenService.generateToken(testUser);
    }

    @Test
    @Transactional
    void shouldVerifyAccessSuccessfully() throws Exception {
        mockMvc.perform(get("/api/profile/verify")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"));
    }

    @Test
    @Transactional
    void shouldReturnProfileSuccessfully() throws Exception {
        mockMvc.perform(get("/api/profile")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Luccas Final"))
                .andExpect(jsonPath("$.account.balance").value(5000.00))
                .andExpect(jsonPath("$.cards").isArray())
                .andExpect(jsonPath("$.cards[0].cardNumber").exists());
    }

    @Test
    @Transactional
    void shouldUpdateProfileSuccessfully() throws Exception {
        UserProfileUpdateDTO updateRequest = new UserProfileUpdateDTO(
                "Luccas Atualizado",
                "luccas.update@email.com",
                "81900000000"
        );

        mockMvc.perform(put("/api/profile")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Luccas Atualizado"))
                .andExpect(jsonPath("$.email").value("luccas.update@email.com"))
                .andExpect(jsonPath("$.telephone").value("81900000000"));


        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertEquals("Luccas Atualizado", updatedUser.getName());
        assertEquals("luccas.update@email.com", updatedUser.getEmail());
    }

    @Test
    @Transactional
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        mockMvc.perform(get("/api/profile"))
                .andExpect(status().isUnauthorized());
    }
}