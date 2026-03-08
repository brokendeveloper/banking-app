package com.brokendev.backend.boleto_payment.controller;

import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.common.domain.user.UserRepository;
import com.brokendev.backend.account.domain.Account;
import com.brokendev.backend.account.domain.AccountRepository;
import com.brokendev.backend.boleto_payment.domain.BoletoPaymentRepository;
import com.brokendev.backend.boleto_payment.dto.BoletoPaymentRequestDTO;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BoletoPaymentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BoletoPaymentRepository boletoPaymentRepository;

    @Autowired
    private TokenService tokenService;

    private String validToken;
    private User testUser;
    private Account testAccount;

    @BeforeEach
    void setUp() {

        boletoPaymentRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();


        testUser = new User();
        testUser.setName("Luccas Boleto Test");
        testUser.setEmail("luccas.boleto@email.com");
        testUser.setCpf("10987654321");
        testUser.setPassword("senhaSegura123");
        testUser.setTelephone("81988888888");
        userRepository.save(testUser);


        testAccount = new Account();
        testAccount.setUser(testUser);
        testAccount.setAccountNumber("98765-4");
        testAccount.setBalance(new BigDecimal("1000.00"));
        accountRepository.save(testAccount);


        validToken = tokenService.generateToken(testUser);
    }

    @Test
    @Transactional
    void shouldPerformBoletoPaymentSuccessfully() throws Exception {
        BoletoPaymentRequestDTO requestDTO = new BoletoPaymentRequestDTO(
                "12345678901234567890", new BigDecimal("250.00")
        );


        mockMvc.perform(post("/api/boleto/pay")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Payment successful!"));


        Account updatedAccount = accountRepository.findByUserEmail(testUser.getEmail()).orElseThrow();
        assertEquals(0, new BigDecimal("750.00").compareTo(updatedAccount.getBalance()));
    }

    @Test
    @Transactional
    void shouldReturnBadRequestWhenBalanceIsInsufficient() throws Exception {

        BoletoPaymentRequestDTO invalidRequest = new BoletoPaymentRequestDTO(
                "12345678901234567890", new BigDecimal("1500.00")
        );

        mockMvc.perform(post("/api/boleto/pay")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void shouldReturnBadRequestWhenAmountIsInvalid() throws Exception {

        BoletoPaymentRequestDTO invalidRequest = new BoletoPaymentRequestDTO(
                "12345678901234567890", new BigDecimal("-50.00")
        );

        mockMvc.perform(post("/api/boleto/pay")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}