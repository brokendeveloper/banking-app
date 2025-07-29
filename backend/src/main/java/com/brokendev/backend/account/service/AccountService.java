package com.brokendev.backend.account.service;

import com.brokendev.backend.account.domain.Account;
import com.brokendev.backend.account.domain.AccountRepository;
import com.brokendev.backend.boleto_payment.domain.BoletoPaymentRepository;
import com.brokendev.backend.common.domain.user.UserRepository;
import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.account.dto.AccountBalanceResponseDTO;
import com.brokendev.backend.account.dto.AccountDepositResponseDTO;
import com.brokendev.backend.account.dto.TransactionStatementResponseDTO;
import com.brokendev.backend.enums.TransactionType;
import com.brokendev.backend.common.exceptions.AccountNotFoundException;
import com.brokendev.backend.pix_transfer.domain.PixTransactionRepository;
import com.brokendev.backend.repositories.*;
import com.brokendev.backend.services.NotificationService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Service
public class AccountService {

    private final AccountRepository accountRepository;

    private final UserRepository userRepository;

    private final PixTransactionRepository pixTransactionRepository;

    private final BoletoPaymentRepository boletoPaymentRepository;

    private final NotificationService notificationService;

    private final InvestmentRepository investmentRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository, PixTransactionRepository pixTransactionRepository, BoletoPaymentRepository boletoPaymentRepository, NotificationService notificationService, InvestmentRepository investmentRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.pixTransactionRepository = pixTransactionRepository;
        this.boletoPaymentRepository = boletoPaymentRepository;
        this.notificationService = notificationService;
        this.investmentRepository = investmentRepository;
    }

    public AccountBalanceResponseDTO getAccountBalance(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));

        return new AccountBalanceResponseDTO(account.getBalance());
    }

    public AccountDepositResponseDTO performDeposit(String email, BigDecimal amount){
        Account account = accountRepository.findByUserEmail(email)
                .orElseThrow(() -> new AccountNotFoundException("Conta com email fornecido não encontrada"));

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        // Notificação
        notificationService.notify(
                account.getUser(),
                "Depósito realizado", "Depósito de R$ " + amount
        );

        return new AccountDepositResponseDTO(account.getBalance(), "Depósito realizado com sucesso!");


    }

    public List<TransactionStatementResponseDTO> getAccountStatement(String email) {
        Account account = accountRepository.findByUserEmail(email)
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));

        List<TransactionStatementResponseDTO> transactions = new ArrayList<>();

        // PIX enviados
        pixTransactionRepository.findAll().stream()
                .filter(pix -> pix.getSender().getId().equals(account.getId()))
                .forEach(pix -> transactions.add(new TransactionStatementResponseDTO(
                        TransactionType.PIX_SENT,
                        pix.getAmount(),
                        pix.getTimestamp(),
                        "PIX enviado para " + pix.getReceiver().getUser().getEmail()
                )));

        // PIX recebidos
        pixTransactionRepository.findAll().stream()
                .filter(pix -> pix.getReceiver().getId().equals(account.getId()))
                .forEach(pix -> transactions.add(new TransactionStatementResponseDTO(
                        TransactionType.PIX_RECEIVED,
                        pix.getAmount(),
                        pix.getTimestamp(),
                        "PIX recebido de " + pix.getSender().getUser().getEmail()
                )));

        // Pagamentos de boleto
        boletoPaymentRepository.findAll().stream()
                .filter(boleto -> boleto.getPayer().getId().equals(account.getId()))
                .forEach(boleto -> transactions.add(new TransactionStatementResponseDTO(
                        TransactionType.BOLETO_PAYMENT,
                        boleto.getAmount(),
                        boleto.getPaymentDate(),
                        "Pagamento de boleto"
                )));

        // Investimentos
        investmentRepository.findByInvestor(account).forEach(investment ->
                transactions.add(new TransactionStatementResponseDTO(
                        TransactionType.INVESTMENT,
                        investment.getAmount(),
                        investment.getInvestmentDate(),
                        "Investimento em " + investment.getType()
                ))
        );

        // Ordena por data decrescente (mais recente primeiro)
        transactions.sort(Comparator.comparing(TransactionStatementResponseDTO::date).reversed());

        return transactions;
    }


}