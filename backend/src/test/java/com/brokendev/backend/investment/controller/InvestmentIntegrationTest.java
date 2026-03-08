package com.brokendev.backend.investment.controller;

import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.common.domain.user.UserRepository;
import com.brokendev.backend.common.enums.InvestmentType;
import com.brokendev.backend.account.domain.Account;
import com.brokendev.backend.account.domain.AccountRepository;
import com.brokendev.backend.investment.domain.Investment;
import com.brokendev.backend.investment.domain.InvestmentRepository;
import com.brokendev.backend.investment.dto.InvestmentRequestDTO;
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
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InvestmentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private TokenService tokenService;

    private String validToken;
    private User testUser;
    private Account testAccount;
    private Investment maturedInvestment;

    @BeforeEach
    void setUp() {

        investmentRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();


        testUser = new User();
        testUser.setName("Luccas Investor");
        testUser.setEmail("luccas.invest@email.com");
        testUser.setCpf("33322211100");
        testUser.setPassword("senhaSegura123");
        testUser.setTelephone("81955555555");
        userRepository.save(testUser);


        testAccount = new Account();
        testAccount.setUser(testUser);
        testAccount.setAccountNumber("12345-X");
        testAccount.setBalance(new BigDecimal("5000.00"));
        accountRepository.save(testAccount);


        maturedInvestment = new Investment();
        maturedInvestment.setInvestor(testAccount);
        maturedInvestment.setType(InvestmentType.CDB);
        maturedInvestment.setAmount(new BigDecimal("1000.00"));
        maturedInvestment.setInvestmentDate(LocalDateTime.now().minusDays(365));
        maturedInvestment.setExpectedReturn(new BigDecimal("1150.00"));
        maturedInvestment.setMaturityDate(LocalDateTime.now().minusDays(1));
        maturedInvestment.setRedeemed(false);
        investmentRepository.save(maturedInvestment);


        validToken = tokenService.generateToken(testUser);
    }

    @Test
    @Transactional
    void shouldCreateInvestmentSuccessfully() throws Exception {
        InvestmentRequestDTO request = new InvestmentRequestDTO(InvestmentType.LCI, new BigDecimal("500.00"));

        mockMvc.perform(post("/api/investments")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("LCI"))
                .andExpect(jsonPath("$.amount").value(500.00))
                .andExpect(jsonPath("$.redeemed").value(false));
    }

    @Test
    @Transactional
    void shouldListInvestmentsSuccessfully() throws Exception {
        mockMvc.perform(get("/api/investments")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].type").value("CDB"));
    }

    @Test
    @Transactional
    void shouldRedeemMaturedInvestmentSuccessfully() throws Exception {

        mockMvc.perform(post("/api/investments/" + maturedInvestment.getId() + "/redeem")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.redeemed").value(true));
    }
}