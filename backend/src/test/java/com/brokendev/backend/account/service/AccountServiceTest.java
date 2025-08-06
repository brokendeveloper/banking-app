package com.brokendev.backend.account.service;

import com.brokendev.backend.account.domain.Account;
import com.brokendev.backend.account.domain.AccountRepository;
import com.brokendev.backend.account.dto.AccountBalanceResponseDTO;
import com.brokendev.backend.boleto_payment.domain.BoletoPaymentRepository;
import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.common.domain.user.UserRepository;
import com.brokendev.backend.common.exceptions.UserAccountNotFoundException;
import com.brokendev.backend.pix_transfer.domain.PixTransactionRepository;
import com.brokendev.backend.repositories.InvestmentRepository;
import com.brokendev.backend.services.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
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

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");

        testAccount = new Account();
        testAccount.setUser(testUser);
        testAccount.setBalance(new BigDecimal("1000.00"));
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
}
