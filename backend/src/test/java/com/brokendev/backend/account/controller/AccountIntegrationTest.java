package com.brokendev.backend.account.controller;

import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.common.domain.user.UserRepository;
import com.brokendev.backend.account.domain.Account;
import com.brokendev.backend.account.domain.AccountRepository;
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
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TokenService tokenService;

    private String validToken;
    private User testUser;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setName("Luccas Teste");
        testUser.setEmail("luccas.teste@email.com");
        testUser.setCpf("12345678900");
        testUser.setPassword("senhaCriptografada123");
        testUser.setTelephone("81999999999");
        userRepository.save(testUser);

        Account account = new Account();
        account.setUser(testUser);
        account.setBalance(new BigDecimal("1000.00"));
        account.setAccountNumber("12345-6");
        accountRepository.save(account);

        validToken = tokenService.generateToken(testUser);
    }

    @Test
    @Transactional
    void shouldReturnAccountBalanceSuccessfully() throws Exception {
        mockMvc.perform(get("/api/account/balance")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1000.00));
    }

    @Test
    @Transactional
    void shouldPerformDepositSuccessfully() throws Exception {
        // Assumindo que o DTO de depósito recebe um campo "amount"
        Map<String, BigDecimal> depositRequest = Map.of("amount", new BigDecimal("500.00"));

        mockMvc.perform(post("/api/account/deposit")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Deposit made successfully!"))
                .andExpect(jsonPath("$.balance").value(1500.00));
    }

    @Test
    @Transactional
    void shouldReturnBadRequestWhenDepositAmountIsInvalid() throws Exception {
        // Tentando depositar um valor negativo
        Map<String, BigDecimal> invalidDepositRequest = Map.of("amount", new BigDecimal("-100.00"));

        mockMvc.perform(post("/api/account/deposit")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDepositRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void shouldReturnAccountStatementSuccessfully() throws Exception {
        mockMvc.perform(get("/api/account/statement")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}