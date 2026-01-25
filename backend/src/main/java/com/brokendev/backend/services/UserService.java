package com.brokendev.backend.services;

import com.brokendev.backend.account.domain.Account;
import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.account.dto.AccountInfoResponseDTO;
import com.brokendev.backend.dto.card.CardResponseDTO;
import com.brokendev.backend.dto.profile.UserProfileResponseDTO;
import com.brokendev.backend.dto.profile.UserProfileUpdateDTO;
import com.brokendev.backend.dto.profile.UserProfileUpdateResponseDTO;
import com.brokendev.backend.common.exceptions.UserAccountNotFoundException;
import com.brokendev.backend.infra.security.TokenService;
import com.brokendev.backend.account.domain.AccountRepository;
import com.brokendev.backend.repositories.CardRepository;
import com.brokendev.backend.common.domain.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.brokendev.backend.utils.CardUtils.maskCardNumber;

@Service
public class    UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private CardService cardService;

    @Autowired
    private UserRepository userRepository;


    public UserProfileResponseDTO getProfile(User user) {
        Account account = accountRepository.findByUserEmail(user.getEmail())
                .orElseThrow(() -> new UserAccountNotFoundException("Conta não encontrada"));

        var cards = cardRepository.findByAccount(account)
                .stream()
                .map(card -> new CardResponseDTO(
                        card.getId(),
                        maskCardNumber(card.getCardNumber()),
                        card.getHolderName(),
                        card.getExpiration(),
                        card.isBlocked(),
                        card.getCreatedAt()
                ))
                .toList();

        return new UserProfileResponseDTO(
                user.getName(),
                user.getEmail(),
                user.getCpf(),
                user.getTelephone(),
                new AccountInfoResponseDTO(account.getId(), account.getBalance()),
                cards
        );
    }

    @Transactional
    public UserProfileUpdateResponseDTO updateProfile(Long userId, UserProfileUpdateDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        if (dto.name() != null) user.setName(dto.name());
        if (dto.telephone() != null) user.setTelephone(dto.telephone());
        if (dto.email() != null) user.setEmail(dto.email());

        userRepository.save(user);

        return new UserProfileUpdateResponseDTO(
                user.getName(),
                user.getEmail(),
                user.getTelephone()
        );
    }
}
