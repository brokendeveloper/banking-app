package com.brokendev.backend.pix_transfer.service;

import com.brokendev.backend.account.domain.Account;
import com.brokendev.backend.account.domain.AccountRepository;
import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.common.exceptions.InsufficientBalanceException;
import com.brokendev.backend.common.exceptions.PixTransferNotAllowedException;
import com.brokendev.backend.common.exceptions.UserAccountNotFoundException;
import com.brokendev.backend.enums.PixKeyType;
import com.brokendev.backend.enums.PixTransactionStatus;
import com.brokendev.backend.pix_transfer.domain.PixTransaction;
import com.brokendev.backend.pix_transfer.domain.PixTransactionRepository;
import com.brokendev.backend.pix_transfer.dto.PixTransferRequestDTO;
import com.brokendev.backend.pix_transfer.dto.PixTransferResponseDTO;
import com.brokendev.backend.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PixTransferServiceTest {

    @Mock
    private PixTransactionRepository pixTransactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PixTransferService pixTransferService;

    private User senderUser;
    private User receiverUser;
    private Account senderAccount;
    private Account receiverAccount;
    private PixTransferRequestDTO pixTransferRequestDTO;

    @BeforeEach
    void setUp() {
        senderUser = new User();
        senderUser.setEmail("sender@example.com");
        senderUser.setId(1L);

        senderAccount = new Account();
        senderAccount.setUser(senderUser);
        senderAccount.setBalance(new BigDecimal("1000.00"));
        senderAccount.setId(10L);


        receiverUser = new User();
        receiverUser.setEmail("receiver@example.com");
        receiverUser.setId(2L);

        receiverAccount = new Account();
        receiverAccount.setUser(receiverUser);
        receiverAccount.setBalance(new BigDecimal("200.00"));
        receiverAccount.setId(20L);
        receiverUser.setCpf("12345678901");
        receiverUser.setTelephone("5581999999999");


        pixTransferRequestDTO = new PixTransferRequestDTO(
                receiverUser.getEmail(),
                PixKeyType.EMAIL,
                new BigDecimal("150.00")
        );

    }

    @Test
    void shouldPerformPixTransferSuccessfullyWhenKeyTypeIsEmail() {
        // given
        BigDecimal transferAmount = new BigDecimal("150.00");
        PixTransferRequestDTO pixTransferRequestDTO = new PixTransferRequestDTO(
                receiverUser.getEmail(),
                PixKeyType.EMAIL,
                transferAmount
        );

        when(accountRepository.findByUserEmail(senderUser.getEmail())).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByUserEmail(receiverUser.getEmail())).thenReturn(Optional.of(receiverAccount));

        // when
        PixTransferResponseDTO responseDTO = pixTransferService.transferPix(
                senderUser.getEmail(), pixTransferRequestDTO
        );

        // then
        assertNotNull(responseDTO);
        assertEquals(senderUser.getEmail(), responseDTO.senderEmail());
        assertEquals(receiverUser.getEmail(), responseDTO.receiverEmail());
        assertEquals(transferAmount, responseDTO.amount());
        assertEquals(PixTransactionStatus.COMPLETED, responseDTO.status());

        assertEquals(new BigDecimal("850.00"), senderAccount.getBalance());
        assertEquals(new BigDecimal("350.00"), receiverAccount.getBalance());

        verify(accountRepository, times(1)).save(senderAccount);
        verify(accountRepository, times(1)).save(receiverAccount);

        ArgumentCaptor<PixTransaction> transactionCaptor = ArgumentCaptor.forClass(PixTransaction.class);
        verify(pixTransactionRepository, times(1)).save(transactionCaptor.capture());

        PixTransaction savedTransaction = transactionCaptor.getValue();
        assertEquals(transferAmount, savedTransaction.getAmount());

        ArgumentCaptor<String> titleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(notificationService, times(1)).notify(
                eq(senderUser), titleCaptor.capture(), messageCaptor.capture()
        );
        verify(notificationService, times(1)).notify(
                eq(receiverUser), titleCaptor.capture(), messageCaptor.capture()
        );

        List<String> capturedTitles = titleCaptor.getAllValues();
        List<String> capturedMessages = messageCaptor.getAllValues();

        assertEquals("PIX sent", capturedTitles.get(0));
        assertTrue(capturedMessages.get(0).contains(transferAmount.toString()));
        assertTrue(capturedMessages.get(0).contains(receiverUser.getEmail()));

        assertEquals("You received a PIX", capturedTitles.get(1));
        assertTrue(capturedMessages.get(1).contains(transferAmount.toString()));
        assertTrue(capturedMessages.get(1).contains(senderUser.getEmail()));

        verifyNoMoreInteractions(pixTransactionRepository, accountRepository, notificationService);
    }

    @Test
    void shouldPerformPixTransferSuccessfullyWhenKeyTypeIsCpf(){
        // given
        BigDecimal transferAmount = new BigDecimal("150.00");
        PixTransferRequestDTO pixTransferRequestDTO = new PixTransferRequestDTO(
                receiverUser.getCpf(),
                PixKeyType.CPF,
                transferAmount
        );

        when(accountRepository.findByUserEmail(senderUser.getEmail())).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByUserCpf(receiverUser.getCpf())).thenReturn(Optional.of(receiverAccount));

        // when
        PixTransferResponseDTO responseDTO = pixTransferService.transferPix(
                senderUser.getEmail(), pixTransferRequestDTO
        );

        // then
        assertNotNull(responseDTO);
        assertEquals(senderUser.getEmail(), responseDTO.senderEmail());
        assertEquals(receiverUser.getEmail(), responseDTO.receiverEmail());
        assertEquals(transferAmount, responseDTO.amount());
        assertEquals(PixTransactionStatus.COMPLETED, responseDTO.status());

        assertEquals(new BigDecimal("850.00"), senderAccount.getBalance());
        assertEquals(new BigDecimal("350.00"), receiverAccount.getBalance());

        verify(accountRepository, times(1)).save(senderAccount);
        verify(accountRepository, times(1)).save(receiverAccount);

        ArgumentCaptor<PixTransaction> transactionCaptor = ArgumentCaptor.forClass(PixTransaction.class);
        verify(pixTransactionRepository, times(1)).save(transactionCaptor.capture());
        PixTransaction savedTransaction = transactionCaptor.getValue();
        assertEquals(transferAmount, savedTransaction.getAmount());

        ArgumentCaptor<String> titleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificationService, times(1)).notify(
                eq(senderUser), titleCaptor.capture(), messageCaptor.capture()
        );
        verify(notificationService, times(1)).notify(
                eq(receiverUser), titleCaptor.capture(), messageCaptor.capture()
        );

        List<String> capturedMessages = messageCaptor.getAllValues();
        assertTrue(capturedMessages.get(0).contains(transferAmount.toString()));
        assertTrue(capturedMessages.get(1).contains(senderUser.getEmail()));

        verifyNoMoreInteractions(pixTransactionRepository, accountRepository, notificationService);
    }

    @Test
    void shouldPerformPixTransferSuccessfullyWhenKeyTypeIsPhone(){
        // given
        BigDecimal transferAmount = new BigDecimal("150.00");
        PixTransferRequestDTO requestDTO = new PixTransferRequestDTO(
                receiverUser.getTelephone(),
                PixKeyType.PHONE,
                transferAmount
        );

        when(accountRepository.findByUserEmail(senderUser.getEmail())).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByUserTelephone(receiverUser.getTelephone())).thenReturn(Optional.of(receiverAccount));

        // when
        PixTransferResponseDTO response = pixTransferService.transferPix(senderUser.getEmail(), requestDTO);

        // then
        assertNotNull(response);
        assertEquals(senderUser.getEmail(), response.senderEmail());
        assertEquals(receiverUser.getEmail(), response.receiverEmail());
        assertEquals(transferAmount, response.amount());
        assertEquals(PixTransactionStatus.COMPLETED, response.status());


        assertEquals(new BigDecimal("850.00"), senderAccount.getBalance());
        assertEquals(new BigDecimal("350.00"), receiverAccount.getBalance());


        verify(accountRepository, times(1)).save(senderAccount);
        verify(accountRepository, times(1)).save(receiverAccount);


        ArgumentCaptor<PixTransaction> transactionCaptor = ArgumentCaptor.forClass(PixTransaction.class);
        verify(pixTransactionRepository, times(1)).save(transactionCaptor.capture());
        PixTransaction savedTransaction = transactionCaptor.getValue();
        assertEquals(transferAmount, savedTransaction.getAmount());


        ArgumentCaptor<String> titleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificationService, times(1)).notify(eq(senderUser), titleCaptor.capture(), messageCaptor.capture());
        verify(notificationService, times(1)).notify(eq(receiverUser), titleCaptor.capture(), messageCaptor.capture());

        List<String> capturedMessages = messageCaptor.getAllValues();
        assertTrue(capturedMessages.get(0).contains(transferAmount.toString()));
        assertTrue(capturedMessages.get(1).contains(senderUser.getEmail()));

        verifyNoMoreInteractions(pixTransactionRepository, accountRepository, notificationService);
    }


    @Test
    void shouldThrowUserAccountNotFoundExceptionWhenSenderAccountIsNotFound(){
        // given
        BigDecimal transferAmount = new BigDecimal("150.00");
        PixTransferRequestDTO pixTransferRequestDTO = new PixTransferRequestDTO(
                "anyKey",
                PixKeyType.EMAIL,
                transferAmount
        );

        when(accountRepository.findByUserEmail(senderUser.getEmail())).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserAccountNotFoundException.class, () ->
                pixTransferService.transferPix(senderUser.getEmail(), pixTransferRequestDTO)
        );

        verify(accountRepository, times(1)).findByUserEmail(senderUser.getEmail());
        verifyNoMoreInteractions(accountRepository, pixTransactionRepository, notificationService);
    }

    @Test
    void shouldThrowUserAccountNotFoundExceptionWhenReceiverAccountIsNotFound(){
        // given
        BigDecimal transferAmount = new BigDecimal("150.00");
        PixTransferRequestDTO pixTransferRequestDTO = new PixTransferRequestDTO(
                "nonexistent@example.com",
                PixKeyType.EMAIL,
                transferAmount
        );

        when(accountRepository.findByUserEmail(senderUser.getEmail())).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByUserEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserAccountNotFoundException.class, () ->
                pixTransferService.transferPix(senderUser.getEmail(), pixTransferRequestDTO)
        );

        verify(accountRepository, times(1)).findByUserEmail(senderUser.getEmail());
        verify(accountRepository, times(1)).findByUserEmail("nonexistent@example.com");

        verifyNoMoreInteractions(accountRepository, pixTransactionRepository, notificationService);
    }

    @Test
    void shouldThrowInsufficientBalanceExceptionWhenTransferAmountIsGreaterThanBalance(){
        // given
        BigDecimal highTransferAmount = new BigDecimal("1500.00");
        PixTransferRequestDTO requestDTO = new PixTransferRequestDTO(
                receiverUser.getEmail(),
                PixKeyType.EMAIL,
                highTransferAmount
        );

        when(accountRepository.findByUserEmail(senderUser.getEmail())).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByUserEmail(receiverUser.getEmail())).thenReturn(Optional.of(receiverAccount));

        // when & then
        assertThrows(InsufficientBalanceException.class, () ->
                pixTransferService.transferPix(senderUser.getEmail(), requestDTO)
        );

        verify(accountRepository, times(1)).findByUserEmail(senderUser.getEmail());
        verify(accountRepository, times(1)).findByUserEmail(receiverUser.getEmail());

        verifyNoMoreInteractions(accountRepository, pixTransactionRepository, notificationService);
    }

    @Test
    void shouldThrowPixTransferNotAllowedExceptionWhenTransferringToSameAccount(){
        // given
        BigDecimal amount = new BigDecimal("150.00");
        PixTransferRequestDTO requestDTO = new PixTransferRequestDTO(
                senderUser.getEmail(),
                PixKeyType.EMAIL,
                amount
        );

        when(accountRepository.findByUserEmail(senderUser.getEmail())).thenReturn(Optional.of(senderAccount));

        // when & then
        assertThrows(PixTransferNotAllowedException.class, () ->
                pixTransferService.transferPix(senderUser.getEmail(), requestDTO)
                );

        verify(accountRepository, atLeastOnce()).findByUserEmail(senderUser.getEmail());
        verifyNoMoreInteractions(pixTransactionRepository, notificationService);

    }

    @Test
    void shouldThrowUnsupportedOperationExceptionWhenPixKeyTypeIsRandom() {
        // given
        PixTransferRequestDTO requestDTO = new PixTransferRequestDTO(
                "random-key-123",
                PixKeyType.RANDOM,
                new BigDecimal("50.00")
        );

        when(accountRepository.findByUserEmail(senderUser.getEmail())).thenReturn(Optional.of(senderAccount));

        // when & then
        assertThrows(UnsupportedOperationException.class, () ->
                pixTransferService.transferPix(senderUser.getEmail(), requestDTO)
                );

        verify(accountRepository, times(1)).findByUserEmail(senderUser.getEmail());
        verifyNoMoreInteractions(accountRepository, notificationService);
    }
}

