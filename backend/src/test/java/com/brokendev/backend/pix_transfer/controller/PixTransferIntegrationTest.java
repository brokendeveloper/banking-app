package com.brokendev.backend.pix_transfer.controller;

import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.common.domain.user.UserRepository;
import com.brokendev.backend.common.enums.PixKeyType;
import com.brokendev.backend.account.domain.Account;
import com.brokendev.backend.account.domain.AccountRepository;
import com.brokendev.backend.pix_transfer.domain.PixTransactionRepository;
import com.brokendev.backend.pix_transfer.dto.PixTransferRequestDTO;
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
class PixTransferIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PixTransactionRepository pixTransactionRepository;

    @Autowired
    private TokenService tokenService;

    private String senderToken;
    private User senderUser;
    private User receiverUser;

    @BeforeEach
    void setUp() {

        pixTransactionRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();


        senderUser = new User();
        senderUser.setName("Luccas Sender");
        senderUser.setEmail("sender@email.com");
        senderUser.setCpf("12312312312");
        senderUser.setPassword("senha123");
        senderUser.setTelephone("81911111111");
        userRepository.save(senderUser);

        Account senderAccount = new Account();
        senderAccount.setUser(senderUser);
        senderAccount.setAccountNumber("0001-1");
        senderAccount.setBalance(new BigDecimal("1000.00"));
        accountRepository.save(senderAccount);


        receiverUser = new User();
        receiverUser.setName("Luccas Receiver");
        receiverUser.setEmail("receiver@email.com");
        receiverUser.setCpf("98798798798");
        receiverUser.setPassword("senha123");
        receiverUser.setTelephone("81922222222");
        userRepository.save(receiverUser);

        Account receiverAccount = new Account();
        receiverAccount.setUser(receiverUser);
        receiverAccount.setAccountNumber("0002-2");
        receiverAccount.setBalance(new BigDecimal("200.00"));
        accountRepository.save(receiverAccount);


        senderToken = tokenService.generateToken(senderUser);
    }

    @Test
    @Transactional
    void shouldPerformPixTransferSuccessfully() throws Exception {

        PixTransferRequestDTO request = new PixTransferRequestDTO(
                receiverUser.getEmail(),
                PixKeyType.EMAIL,
                new BigDecimal("150.00")
        );


        mockMvc.perform(post("/api/pix/transfer")
                        .header("Authorization", "Bearer " + senderToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.amount").value(150.00));


        Account updatedSender = accountRepository.findByUserEmail(senderUser.getEmail()).orElseThrow();
        Account updatedReceiver = accountRepository.findByUserEmail(receiverUser.getEmail()).orElseThrow();


        assertEquals(0, new BigDecimal("850.00").compareTo(updatedSender.getBalance()));

        assertEquals(0, new BigDecimal("350.00").compareTo(updatedReceiver.getBalance()));
    }

    @Test
    @Transactional
    void shouldReturnBadRequestWhenBalanceIsInsufficient() throws Exception {

        PixTransferRequestDTO invalidRequest = new PixTransferRequestDTO(
                receiverUser.getEmail(),
                PixKeyType.EMAIL,
                new BigDecimal("5000.00")
        );

        mockMvc.perform(post("/api/pix/transfer")
                        .header("Authorization", "Bearer " + senderToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}