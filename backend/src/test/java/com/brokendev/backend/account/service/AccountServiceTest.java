package com.brokendev.backend.account.service;

import com.brokendev.backend.account.domain.Account;
import com.brokendev.backend.account.domain.AccountRepository;
import com.brokendev.backend.account.dto.AccountBalanceResponseDTO;
import com.brokendev.backend.account.dto.AccountDepositResponseDTO;
import com.brokendev.backend.account.dto.TransactionStatementResponseDTO;
import com.brokendev.backend.boleto_payment.domain.BoletoPayment;
import com.brokendev.backend.boleto_payment.domain.BoletoPaymentRepository;
import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.common.domain.user.UserRepository;
import com.brokendev.backend.common.enums.*;
import com.brokendev.backend.common.exceptions.InvalidDepositAmountException;
import com.brokendev.backend.common.exceptions.UserAccountNotFoundException;
import com.brokendev.backend.investment.domain.Investment;
import com.brokendev.backend.pix_transfer.domain.PixTransaction;
import com.brokendev.backend.pix_transfer.domain.PixTransactionRepository;
import com.brokendev.backend.investment.domain.InvestmentRepository;
import com.brokendev.backend.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PixTransactionRepository pixTransactionRepository;

    @Mock
    private BoletoPaymentRepository boletoPaymentRepository;

    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AccountService accountService;

    private User testUser;
    private Account testAccount;
    private Account testAccount2;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");

        testAccount = new Account();
        testAccount.setUser(testUser);
        testAccount.setBalance(new BigDecimal("1000.00"));
        testAccount.setId(1L);

        testAccount2 = new Account();
        testAccount2.setId(2L);
        testAccount2.setUser(new User());
    }

    @Test
    void shouldReturnAccountBalanceWhenUserAndAccountAreFound(){
        // given
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(accountRepository.findByUser(testUser)).thenReturn(Optional.of(testAccount));

        // when
        AccountBalanceResponseDTO result = accountService.getAccountBalance(testUser.getEmail());

        // then
        assertNotNull(result);
        assertEquals(new BigDecimal("1000.00"), result.balance());

        verify(userRepository, times(1)).findByEmail(testUser.getEmail());
        verify(accountRepository, times(1)).findByUser(testUser);
        verifyNoMoreInteractions(userRepository, accountRepository, pixTransactionRepository, boletoPaymentRepository, notificationService);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserIsNotFound(){
        // given
        when(userRepository.findByEmail("non-existent@example.com")).thenReturn(Optional.empty());

        // when & then
        assertThrows(UsernameNotFoundException.class, () ->
                accountService.getAccountBalance("non-existent@example.com")
                );

        verify(userRepository, times(1)).findByEmail("non-existent@example.com");
        verifyNoMoreInteractions(userRepository, accountRepository, pixTransactionRepository, boletoPaymentRepository, notificationService);
    }

    @Test
    void shouldThrowAccountNotFoundExceptionWhenAccountIsNotFound(){
        // given
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(accountRepository.findByUser(testUser)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserAccountNotFoundException.class, () ->
                accountService.getAccountBalance(testUser.getEmail())
                );

        verify(userRepository, times(1)).findByEmail(testUser.getEmail());
        verify(accountRepository, times(1)).findByUser(testUser);
        verifyNoMoreInteractions(userRepository, accountRepository, pixTransactionRepository, boletoPaymentRepository, notificationService);
    }


    @Test
    void shouldPerformDepositSuccessfullyWhenAccountIsFound(){
        // given
        BigDecimal depositAmount = new BigDecimal("500.00");
        BigDecimal expectedBalance = new BigDecimal("1500.00");

        when(accountRepository.findByUserEmail(testUser.getEmail())).thenReturn(Optional.of(testAccount));

        // when
        AccountDepositResponseDTO result = accountService.performDeposit(testUser.getEmail(), depositAmount);

        // then
        assertNotNull(result);
        assertEquals(expectedBalance, result.balance());
        assertEquals("Deposit made successfully!", result.message());

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository, times(1)).save(accountCaptor.capture());

        Account savedAccount = accountCaptor.getValue();
        assertEquals(expectedBalance, savedAccount.getBalance());

        verify(notificationService, times(1)).notify(
                testUser,
                "Deposit made", "Deposit of R$ " + depositAmount
        );

        verifyNoMoreInteractions(accountRepository, accountRepository, pixTransactionRepository, boletoPaymentRepository, notificationService);
    }

    @Test
    void shouldThrowInvalidDepositAmountExceptionWhenAmountIsInvalid(){
        // given
        BigDecimal invalidAmount = new BigDecimal("-500.00");
        String expectedMessage = "The deposit amount must be greater than zero";

        // when & then
        InvalidDepositAmountException exception = assertThrows(
                InvalidDepositAmountException.class, () ->
                        accountService.performDeposit(testUser.getEmail(), invalidAmount)
        );

        assertEquals(expectedMessage, exception.getMessage());
        verifyNoMoreInteractions(accountRepository, accountRepository, pixTransactionRepository, notificationService);
    }

    @Test
    void shouldThrowUserAccountNotFoundExceptionWhenAccountIsNotFound(){
        // given
        BigDecimal validDepositAmount = new BigDecimal("100.00");
        when(accountRepository.findByUserEmail(testUser.getEmail())).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserAccountNotFoundException.class, () ->
                accountService.performDeposit(testUser.getEmail(), validDepositAmount)
                );

        verify(accountRepository, times(1)).findByUserEmail(testUser.getEmail());
        verifyNoMoreInteractions(userRepository, accountRepository, pixTransactionRepository, boletoPaymentRepository, notificationService);
    }


    @Test
    void shouldReturnEmptyStatementWhenAccountHasNoTransactions(){
        // given
        when(accountRepository.findByUserEmail(testUser.getEmail())).thenReturn(Optional.of(testAccount));

        when(pixTransactionRepository.findAll()).thenReturn(Collections.emptyList());
        when(boletoPaymentRepository.findAll()).thenReturn(Collections.emptyList());
        when(investmentRepository.findByInvestor(testAccount)).thenReturn(Collections.emptyList());

        // when
        List<TransactionStatementResponseDTO> transactions = accountService.getAccountStatement(testUser.getEmail());

        // then
        assertNotNull(transactions);
        assertTrue(transactions.isEmpty());

        verify(accountRepository, times(1)).findByUserEmail(testUser.getEmail());
        verify(pixTransactionRepository, times(2)).findAll();
        verify(boletoPaymentRepository, times(1)).findAll();
        verify(investmentRepository, times(1)).findByInvestor(testAccount);
        verifyNoMoreInteractions(accountRepository, pixTransactionRepository, boletoPaymentRepository, notificationService);
    }

    @Test
    void shouldReturnFullStatementWhenAccountHasTransactions(){
        // given
        when(accountRepository.findByUserEmail(testUser.getEmail())).thenReturn(Optional.of(testAccount));

        PixTransaction pixSent = new PixTransaction(
                1L, testAccount, testAccount2, new BigDecimal("50.00"),
                LocalDateTime.now().minusHours(2), PixTransactionStatus.COMPLETED, "PIX sent",
                PixKeyType.EMAIL, "recipient@email.com"
        );
        PixTransaction pixReceived = new PixTransaction(
                2L, testAccount2, testAccount, new BigDecimal("100.00"),
                LocalDateTime.now().minusHours(1), PixTransactionStatus.COMPLETED, "PIX received",
                PixKeyType.CPF, "12345678901");
        when(pixTransactionRepository.findAll()).thenReturn(Arrays.asList(pixSent, pixReceived));

        BoletoPayment boletoPayment = new BoletoPayment(
                1L, testAccount, "barcode123", new BigDecimal("300.00"),
                LocalDateTime.now().minusHours(3), BoletoPaymentStatus.PAID, "Boleto 1"
        );
        BoletoPayment irrelevantBoleto = new BoletoPayment(
                2L, testAccount2, "barcode456", new BigDecimal("100.00"),
                LocalDateTime.now().minusHours(4), BoletoPaymentStatus.PAID, "Boleto 2"
        );
        when(boletoPaymentRepository.findAll()).thenReturn(Arrays.asList(boletoPayment, irrelevantBoleto));

        Investment investment = new Investment(
                1L, testAccount, InvestmentType.CDB, new BigDecimal("200.00"),
                LocalDateTime.now(), new BigDecimal("250.00"), LocalDateTime.now().plusMonths(6), false
        );
        when(investmentRepository.findByInvestor(testAccount)).thenReturn(Arrays.asList(investment));

        // when
        List<TransactionStatementResponseDTO> statement = accountService.getAccountStatement(testUser.getEmail());

        // then
        assertEquals(4, statement.size());

        assertEquals(TransactionType.INVESTMENT, statement.get(0).type());
        assertEquals(new BigDecimal("200.00"), statement.get(0).amount());

        assertEquals(TransactionType.PIX_RECEIVED, statement.get(1).type());
        assertEquals(new BigDecimal("100.00"), statement.get(1).amount());

        assertEquals(TransactionType.PIX_SENT, statement.get(2).type());
        assertEquals(new BigDecimal("50.00"), statement.get(2).amount());

        assertEquals(TransactionType.BOLETO_PAYMENT, statement.get(3).type());
        assertEquals(new BigDecimal("300.00"), statement.get(3).amount());

        verify(accountRepository, times(1)).findByUserEmail(testUser.getEmail());
        verify(pixTransactionRepository, times(2)).findAll();
        verify(boletoPaymentRepository, times(1)).findAll();
        verify(investmentRepository, times(1)).findByInvestor(testAccount);

        verifyNoMoreInteractions(accountRepository, pixTransactionRepository, boletoPaymentRepository, investmentRepository);
    }

    @Test
    void shouldThrowUserAccountNotFoundExceptionWhenGettingStatementForNonExistentAccount() {
        // given
        when(accountRepository.findByUserEmail(testUser.getEmail())).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserAccountNotFoundException.class, () ->
                accountService.getAccountStatement(testUser.getEmail())
        );

        verify(accountRepository, times(1)).findByUserEmail(testUser.getEmail());

        verifyNoInteractions(pixTransactionRepository, boletoPaymentRepository, investmentRepository);
    }

}





