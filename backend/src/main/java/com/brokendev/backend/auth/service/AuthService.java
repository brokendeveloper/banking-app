package com.brokendev.backend.auth.service;

import com.brokendev.backend.auth.dto.login.LoginRequestDTO;
import com.brokendev.backend.auth.dto.login.LoginResponseDTO;
import com.brokendev.backend.auth.dto.register.RegisterRequestDTO;
import com.brokendev.backend.auth.dto.register.RegisterResponseDTO;
import com.brokendev.backend.domain.Account;
import com.brokendev.backend.domain.User;
import com.brokendev.backend.common.exceptions.InvalidPasswordException;
import com.brokendev.backend.common.exceptions.UserAlreadyExistsException;
import com.brokendev.backend.infra.security.TokenService;
import com.brokendev.backend.repositories.AccountRepository;
import com.brokendev.backend.repositories.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.brokendev.backend.utils.AccountUtils.generateAccountNumber;

@Service
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenService tokenService;

    private final AccountRepository accountRepository;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.accountRepository = accountRepository;
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new InvalidPasswordException("Invalid password");
        }
        String token = tokenService.generateToken(user);

        return new LoginResponseDTO(user.getName(), user.getEmail(), token);
    }


    public RegisterResponseDTO register(RegisterRequestDTO registerRequest) {
        if(userRepository.findByEmail(registerRequest.email()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        User user = new User();
        user.setEmail(registerRequest.email());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user.setName(registerRequest.name());
        user.setCpf(registerRequest.cpf());
        user.setTelephone(registerRequest.telephone());
        userRepository.save(user);

        Account account = new Account();
        account.setUser(user);
        account.setBalance(BigDecimal.ZERO);
        account.setAccountNumber(generateAccountNumber());
        accountRepository.save(account);

        return new RegisterResponseDTO(user.getName(), user.getEmail(), "User registered successfully");
    }


}
