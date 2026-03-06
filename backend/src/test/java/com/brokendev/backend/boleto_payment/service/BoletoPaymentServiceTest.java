package com.brokendev.backend.boleto_payment.service;

import com.brokendev.backend.account.domain.Account;
import com.brokendev.backend.account.domain.AccountRepository;
import com.brokendev.backend.boleto_payment.domain.BoletoPayment;
import com.brokendev.backend.boleto_payment.domain.BoletoPaymentRepository;
import com.brokendev.backend.boleto_payment.dto.BoletoPaymentRequestDTO;
import com.brokendev.backend.boleto_payment.dto.BoletoPaymentResponseDTO;
import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.common.exceptions.InsufficientBalanceException;
import com.brokendev.backend.common.exceptions.InvalidBoletoAmountException;
import com.brokendev.backend.common.exceptions.UserAccountNotFoundException;
import com.brokendev.backend.enums.BoletoPaymentStatus;
import com.brokendev.backend.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoletoPaymentServiceTest {


    @Mock
    private BoletoPaymentRepository boletoPaymentRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private BoletoPaymentService boletoPaymentService;

    private User testUser;
    private Account testAccount;
    private BoletoPaymentRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");

        testAccount = new Account();
        testAccount.setUser(testUser);
        testAccount.setBalance(new BigDecimal("1000.00"));

        requestDTO = new BoletoPaymentRequestDTO(
                "12345678901234567890", new BigDecimal("250.00")
        );
    }

    @Test
    void shouldPerformBoletoPaymentSuccessfullyAndUpdateBalance() {
        // given
        when(accountRepository.findByUserEmail(testUser.getEmail())).thenReturn(Optional.of(testAccount));

        // when
        BoletoPaymentResponseDTO response = boletoPaymentService.payBoleto(testUser.getEmail(), requestDTO);

        // then
        assertNotNull(response);
        assertEquals("Payment successful!", response.description());

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository, times(1)).save(accountCaptor.capture());

        Account savedAccount = accountCaptor.getValue();
        assertEquals(new BigDecimal("750.00"), savedAccount.getBalance());

        ArgumentCaptor<BoletoPayment> boletoPaymentCaptor = ArgumentCaptor.forClass(BoletoPayment.class);
        verify(boletoPaymentRepository, times(1)).save(boletoPaymentCaptor.capture());

        BoletoPayment savedBoletoPayment = boletoPaymentCaptor.getValue();
        assertEquals(requestDTO.barcode(), savedBoletoPayment.getBarcode());
        assertEquals(requestDTO.amount(), savedBoletoPayment.getAmount());
        assertEquals(BoletoPaymentStatus.PAID, savedBoletoPayment.getStatus());

        verify(notificationService,times(1)).notify(
                testUser,
                "Boleto payment successful!",
                "You paid a R$ " + requestDTO.amount() + " (code: " + requestDTO.barcode() + " )"
                );

        verifyNoMoreInteractions(boletoPaymentRepository, accountRepository, notificationService);
    }

    @Test
    void shouldThrowInsufficentBalanceExceptionWhenBalanceIsLessThanBoletoAmount() {
        // given
        BigDecimal insufficentBalanceAmount = new BigDecimal("1500.00");
        BoletoPaymentRequestDTO invalidRequest = new BoletoPaymentRequestDTO(
                "19920392003", insufficentBalanceAmount
        );

        when(accountRepository.findByUserEmail(testUser.getEmail())).thenReturn(Optional.of(testAccount));

        // when & then
        assertThrows(InsufficientBalanceException.class, () ->
                boletoPaymentService.payBoleto(testUser.getEmail(), invalidRequest)
        );

        verify(accountRepository, times(1)).findByUserEmail(testUser.getEmail());
        verifyNoMoreInteractions(accountRepository, notificationService, boletoPaymentRepository);
    }

    @Test
    void shouldThrowUserAccountNotFoundExceptionWhenAccountDoesNotExist() {
        // given
        BoletoPaymentRequestDTO boletoPaymentRequestDTO = new BoletoPaymentRequestDTO(
                "19920392003", new BigDecimal("100.00")
        );
        when(accountRepository.findByUserEmail(testUser.getEmail())).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserAccountNotFoundException.class, () ->
                boletoPaymentService.payBoleto(testUser.getEmail(), boletoPaymentRequestDTO)
        );

        verify(accountRepository, times(1)).findByUserEmail(testUser.getEmail());
        verifyNoMoreInteractions(accountRepository, notificationService, boletoPaymentRepository);

    }

    @Test
    void shouldThrowInvalidBoletoAmountExceptionWhenAmountIsInvalid() {
        // given
        BoletoPaymentRequestDTO invalidRequest = new BoletoPaymentRequestDTO(
                "19920392003", new BigDecimal("-10.00")
        );

        // when & then
        assertThrows(InvalidBoletoAmountException.class, () ->
                boletoPaymentService.payBoleto(testUser.getEmail(), invalidRequest)
        );

        verifyNoMoreInteractions(accountRepository, notificationService, boletoPaymentRepository);

    }
}
