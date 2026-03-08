package com.brokendev.backend.profile.service;

import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.common.domain.user.UserRepository;
import com.brokendev.backend.common.exceptions.UserAccountNotFoundException;
import com.brokendev.backend.account.domain.Account;
import com.brokendev.backend.account.domain.AccountRepository;
import com.brokendev.backend.card.domain.Card;
import com.brokendev.backend.card.domain.CardRepository;
import com.brokendev.backend.profile.dto.UserProfileResponseDTO;
import com.brokendev.backend.profile.dto.UserProfileUpdateDTO;
import com.brokendev.backend.profile.dto.UserProfileUpdateResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private CardRepository cardRepository;

    @InjectMocks
    private UserProfileService userProfileService;

    private User user;
    private Account account;
    private Card card;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Luccas Profile");
        user.setEmail("luccas.profile@email.com");
        user.setCpf("12345678900");
        user.setTelephone("81999999999");

        account = new Account();
        account.setId(10L);
        account.setUser(user);
        account.setBalance(new BigDecimal("1500.00"));

        card = new Card();
        card.setId(100L);
        card.setAccount(account);
        card.setHolderName("Luccas Profile");
        card.setCardNumber("1234567890123456");
        card.setExpiration("12/30");
        card.setBlocked(false);
        card.setCreatedAt(LocalDate.now());
    }

    @Test
    void getProfile_givenValidUser_whenAccountExists_thenReturnProfile() {
        when(accountRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(account));
        when(cardRepository.findByAccount(account)).thenReturn(List.of(card));

        UserProfileResponseDTO response = userProfileService.getProfile(user);

        assertThat(response.name()).isEqualTo("Luccas Profile");
        assertThat(response.email()).isEqualTo("luccas.profile@email.com");
        assertThat(response.account().balance()).isEqualByComparingTo("1500.00");
        assertThat(response.cards()).hasSize(1);


        assertThat(response.cards().get(0).cardNumber()).contains("****");
    }

    @Test
    void getProfile_givenValidUser_whenAccountNotFound_thenThrowException() {
        when(accountRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userProfileService.getProfile(user))
                .isInstanceOf(UserAccountNotFoundException.class)
                .hasMessageContaining("Account not found");
    }

    @Test
    void updateProfile_givenValidData_whenUserExists_thenUpdateAndReturnProfile() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserProfileUpdateDTO updateDTO = new UserProfileUpdateDTO(
                "Luccas Updated",
                "luccas.new@email.com",
                "81988888888"
        );

        UserProfileUpdateResponseDTO response = userProfileService.updateProfile(1L, updateDTO);

        assertThat(response.name()).isEqualTo("Luccas Updated");
        assertThat(response.email()).isEqualTo("luccas.new@email.com");
        assertThat(response.telephone()).isEqualTo("81988888888");

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateProfile_givenPartialData_whenUserExists_thenUpdateOnlyProvidedFields() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));


        UserProfileUpdateDTO updateDTO = new UserProfileUpdateDTO("Luccas Partial", null, null);

        UserProfileUpdateResponseDTO response = userProfileService.updateProfile(1L, updateDTO);

        assertThat(response.name()).isEqualTo("Luccas Partial");
        assertThat(response.email()).isEqualTo("luccas.profile@email.com");
    }

    @Test
    void updateProfile_givenInvalidUserId_whenUserNotFound_thenThrowException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        UserProfileUpdateDTO updateDTO = new UserProfileUpdateDTO("Name", "email@email.com", "123");

        assertThatThrownBy(() -> userProfileService.updateProfile(99L, updateDTO))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
    }
}