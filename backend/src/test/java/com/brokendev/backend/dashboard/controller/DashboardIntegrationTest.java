package com.brokendev.backend.dashboard.controller;

import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.common.domain.user.UserRepository;
import com.brokendev.backend.account.domain.Account;
import com.brokendev.backend.account.domain.AccountRepository;
import com.brokendev.backend.infra.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DashboardIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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
        testUser.setName("Luccas Dashboard");
        testUser.setEmail("luccas.dash@email.com");
        testUser.setCpf("99988877766");
        testUser.setPassword("senhaSegura123");
        testUser.setTelephone("81966666666");
        userRepository.save(testUser);


        Account testAccount = new Account();
        testAccount.setUser(testUser);
        testAccount.setAccountNumber("102030-4");
        testAccount.setBalance(new BigDecimal("2500.50"));
        accountRepository.save(testAccount);


        validToken = tokenService.generateToken(testUser);
    }

    @Test
    @Transactional
    void shouldReturnDashboardInformationSuccessfully() throws Exception {

        mockMvc.perform(get("/api/dashboard")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Luccas Dashboard"))
                .andExpect(jsonPath("$.email").value("luccas.dash@email.com"))
                .andExpect(jsonPath("$.balance").value(2500.50))
                .andExpect(jsonPath("$.lastTransactions").isArray());
    }

    @Test
    @Transactional
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isUnauthorized());
    }
}