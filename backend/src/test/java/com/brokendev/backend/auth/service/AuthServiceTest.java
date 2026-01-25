package com.brokendev.backend.auth.service;

import com.brokendev.backend.auth.dto.login.LoginRequestDTO;
import com.brokendev.backend.auth.dto.login.LoginResponseDTO;
import com.brokendev.backend.auth.dto.register.RegisterRequestDTO;
import com.brokendev.backend.auth.dto.register.RegisterResponseDTO;
import com.brokendev.backend.account.domain.AccountRepository;
import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.common.domain.user.UserRepository;
import com.brokendev.backend.common.exceptions.InvalidPasswordException;
import com.brokendev.backend.common.exceptions.UserAlreadyExistsException;
import com.brokendev.backend.infra.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AuthService authService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.setName("Test User");
        existingUser.setEmail("test@email.com");
        existingUser.setPassword("encodedPassword");
        existingUser.setCpf("12345678900");
        existingUser.setTelephone("999998888");

    }

    @Test
    void shouldLoginSuccessfullyWhenCredentialsAreValid() {
        // given
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("test@email.com","testPassword123");
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("testPassword123", "encodedPassword")).thenReturn(true);
        when(tokenService.generateToken(existingUser)).thenReturn("token");

        // when
        LoginResponseDTO loginResponseDTO = authService.login(loginRequestDTO);

        // then
        assertThat(loginResponseDTO.name()).isEqualTo("Test User");
        assertThat(loginResponseDTO.email()).isEqualTo("test@email.com");
        assertThat(loginResponseDTO.token()).isEqualTo("token");
    }


    @Test
    void shouldThrowUsernameNotFoundExceptionWhenUserNotFoundByEmail() {
        // given
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("notfound@email.com", "password123");
        when(userRepository.findByEmail("notfound@email.com")).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequestDTO))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");

    }

    @Test
    void shouldThrowInvalidPasswordExceptionWhenPasswordIsIncorrect() {
        // given
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("test@email.com", "wrongPassword123");
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("wrongPassword123", "encodedPassword")).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequestDTO))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessage("Invalid password");
    }


    @Test
    void shouldRegisterSuccessfullyWhenUserDataIsValidAndEmailIsAvailable() {
        // given
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO(
                "New Test User", "12345678910", "newusertest@email.com", "newUserPassword",
                "912345678"
                );
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newUserPassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        RegisterResponseDTO registerResponseDTO = authService.register(registerRequestDTO);

        // then
        assertThat(registerResponseDTO.name()).isEqualTo("New Test User");
        assertThat(registerResponseDTO.email()).isEqualTo("newusertest@email.com");
        assertThat(registerResponseDTO.message()).isEqualTo("User registered successfully");

    }

    @Test
    void shouldThrowUserAlreadyExistsExceptionWhenEmailIsAlreadyRegistered() {
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO(
                "New User Test", "19929939900", "test@email.com", "testPassword123",
                "912345670"
                );
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(existingUser));

        // when & then
        assertThatThrownBy(() -> authService.register(registerRequestDTO))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Email already exists");
    }

}
