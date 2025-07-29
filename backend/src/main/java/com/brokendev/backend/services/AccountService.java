package com.brokendev.backend.services;

import com.brokendev.backend.account.domain.Account;
import com.brokendev.backend.account.domain.AccountRepository;
import com.brokendev.backend.boleto_payment.domain.BoletoPaymentRepository;
import com.brokendev.backend.common.domain.user.UserRepository;
import com.brokendev.backend.boleto_payment.domain.BoletoPayment;
import com.brokendev.backend.pix_transfer.domain.PixTransaction;
import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.account.dto.AccountBalanceResponseDTO;
import com.brokendev.backend.account.dto.AccountDepositResponseDTO;
import com.brokendev.backend.account.dto.TransactionStatementResponseDTO;
import com.brokendev.backend.boleto_payment.dto.BoletoPaymentRequestDTO;
import com.brokendev.backend.boleto_payment.dto.BoletoPaymentResponseDTO;
import com.brokendev.backend.pix_transfer.dto.PixTransferRequestDTO;
import com.brokendev.backend.pix_transfer.dto.PixTransferResponseDTO;
import com.brokendev.backend.enums.BoletoPaymentStatus;
import com.brokendev.backend.enums.PixTransactionStatus;
import com.brokendev.backend.enums.TransactionType;
import com.brokendev.backend.common.exceptions.AccountNotFoundException;
import com.brokendev.backend.common.exceptions.InsufficientBalanceException;
import com.brokendev.backend.common.exceptions.PixTransferNotAllowedException;
import com.brokendev.backend.pix_transfer.domain.PixTransactionRepository;
import com.brokendev.backend.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    private void sendAccountNotification(Account account, String title, String message) {
        User user = account.getUser();
        notificationService.notify(user, title, message);

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
        sendAccountNotification(account, "Depósito realizado", "Depósito de R$ " + amount);

        return new AccountDepositResponseDTO(account.getBalance(), "Depósito realizado com sucesso!");


    }



    public BoletoPaymentResponseDTO payBoleto(String payerEmail, BoletoPaymentRequestDTO request) {
        Account payer = accountRepository.findByUserEmail(payerEmail)
                .orElseThrow(() -> new AccountNotFoundException("Conta com email fornecido não encontrada"));

        if(payer.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientBalanceException("Saldo insuficiente");
        }

        payer.setBalance(payer.getBalance().subtract(request.amount()));
        accountRepository.save(payer);

        BoletoPayment boleto = new BoletoPayment();
        boleto.setPayer(payer);
        boleto.setBarcode(request.barcode());
        boleto.setAmount(request.amount());
        boleto.setPaymentDate(LocalDateTime.now());
        boleto.setStatus(BoletoPaymentStatus.PAID);
        boleto.setDescription("Pagamento de boleto realizado com sucesso!");
        boletoPaymentRepository.save(boleto);

        // Notificação
        sendAccountNotification(
                payer,
                "Boleto pago",
                "Você pagou um boleto de R$ " + request.amount() + " (código: " + request.barcode() + " )"
        );

        return new BoletoPaymentResponseDTO(
                boleto.getBarcode(),
                boleto.getAmount(),
                boleto.getPaymentDate(),
                boleto.getStatus(),
                boleto.getDescription()
        );
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