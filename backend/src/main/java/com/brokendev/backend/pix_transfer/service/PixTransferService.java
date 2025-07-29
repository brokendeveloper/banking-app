package com.brokendev.backend.pix_transfer.service;

import com.brokendev.backend.account.domain.Account;
import com.brokendev.backend.account.domain.AccountRepository;
import com.brokendev.backend.common.exceptions.AccountNotFoundException;
import com.brokendev.backend.common.exceptions.InsufficientBalanceException;
import com.brokendev.backend.common.exceptions.PixTransferNotAllowedException;
import com.brokendev.backend.enums.PixTransactionStatus;
import com.brokendev.backend.pix_transfer.domain.PixTransaction;
import com.brokendev.backend.pix_transfer.domain.PixTransactionRepository;
import com.brokendev.backend.pix_transfer.dto.PixTransferRequestDTO;
import com.brokendev.backend.pix_transfer.dto.PixTransferResponseDTO;
import com.brokendev.backend.services.NotificationService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PixTransferService {

    private final PixTransactionRepository pixTransactionRepository;

    private final AccountRepository accountRepository;

    private final NotificationService notificationService;


    public PixTransferService(PixTransactionRepository pixTransactionRepository, AccountRepository accountRepository, NotificationService notificationService) {
        this.pixTransactionRepository = pixTransactionRepository;
        this.accountRepository = accountRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public PixTransferResponseDTO transferPix(String senderEmail, PixTransferRequestDTO request) {
        Account sender = accountRepository.findByUserEmail(senderEmail)
                .orElseThrow(() -> new AccountNotFoundException("Conta do remetente não encontrada"));

        Account receiver = switch (request.pixKeyType()) {
            case EMAIL -> accountRepository.findByUserEmail(request.pixKey())
                    .orElseThrow(() -> new AccountNotFoundException("Conta do destinatário não encontrada"));
            case CPF -> accountRepository.findByUserCpf(request.pixKey())
                    .orElseThrow(() -> new AccountNotFoundException("Conta do destinatário não encontrada"));
            case PHONE -> accountRepository.findByUserTelephone(request.pixKey())
                    .orElseThrow(() -> new AccountNotFoundException("Conta do destinatário não encontrada"));
            case RANDOM -> throw new UnsupportedOperationException("Chave aleatória ainda não suportada");
        };

        if (sender.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientBalanceException("Saldo insuficiente");
        }
        if (sender.getId().equals(receiver.getId())) {
            throw new PixTransferNotAllowedException("Não é permitido transferir para si mesmo");
        }

        sender.setBalance(sender.getBalance().subtract(request.amount()));
        receiver.setBalance(receiver.getBalance().add(request.amount()));
        accountRepository.save(sender);
        accountRepository.save(receiver);

        PixTransaction transaction = new PixTransaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(request.amount());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus(PixTransactionStatus.COMPLETED);
        transaction.setDescription("Transferência PIX realizada com sucesso!");
        transaction.setPixKeyType(request.pixKeyType());
        transaction.setPixKey(request.pixKey());
        pixTransactionRepository.save(transaction);

        // Notificações
        notificationService.notify(
                sender.getUser(),
                "PIX enviado", "Você enviou um PIX de R$ " +
                        request.amount() + " para " + receiver.getUser().getEmail()
        );

        notificationService.notify(
                receiver.getUser(),
                "PIX recebido", "Você recebeu um PIX de R$ " + request.amount() +
                        " de " + sender.getUser().getEmail()
        );

        return new PixTransferResponseDTO(
                sender.getUser().getEmail(),
                receiver.getUser().getEmail(),
                request.amount(),
                transaction.getTimestamp(),
                transaction.getStatus(),
                transaction.getDescription(),
                transaction.getPixKeyType(),
                transaction.getPixKey()
        );
    }
}
