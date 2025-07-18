package com.brokendev.backend.services;

import com.brokendev.backend.domain.Account;
import com.brokendev.backend.domain.BoletoPayment;
import com.brokendev.backend.domain.PixTransaction;
import com.brokendev.backend.domain.User;
import com.brokendev.backend.dto.account.AccountBalanceResponseDTO;
import com.brokendev.backend.dto.account.AccountDepositResponseDTO;
import com.brokendev.backend.dto.account.TransactionStatementResponseDTO;
import com.brokendev.backend.dto.boleto.BoletoPaymentRequestDTO;
import com.brokendev.backend.dto.boleto.BoletoPaymentResponseDTO;
import com.brokendev.backend.dto.pixTransfer.PixTransferRequestDTO;
import com.brokendev.backend.dto.pixTransfer.PixTransferResponseDTO;
import com.brokendev.backend.enums.BoletoPaymentStatus;
import com.brokendev.backend.enums.PixTransactionStatus;
import com.brokendev.backend.enums.TransactionType;
import com.brokendev.backend.exception.AccountNotFoundException;
import com.brokendev.backend.exception.InsufficientBalanceException;
import com.brokendev.backend.exception.PixTransferNotAllowedException;
import com.brokendev.backend.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PixTransactionRepository pixTransactionRepository;

    @Autowired
    private BoletoPaymentRepository boletoPaymentRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private InvestmentRepository investmentRepository;

    public AccountBalanceResponseDTO getBalance(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));

        return new AccountBalanceResponseDTO(account.getBalance());
    }

    public AccountDepositResponseDTO deposit(String email, BigDecimal amount){
        Account account = accountRepository.findByUserEmail(email)
                .orElseThrow(() -> new AccountNotFoundException("Conta com email fornecido não encontrada"));

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        // Notificação
        User user = account.getUser();
        notificationService.notify(
                user,
                "Depósito realizado",
                "Você recebeu um depósito de R$ " + amount
        );

        return new AccountDepositResponseDTO(account.getBalance(), "Depósito realizado com sucesso!");


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
                "PIX enviado",
                "Você enviou um PIX de R$ " + request.amount() + " para " + receiver.getUser().getEmail()
        );
        notificationService.notify(
                receiver.getUser(),
                "PIX recebido",
                "Você recebeu um PIX de R$ " + request.amount() + " de " + sender.getUser().getEmail()
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
        notificationService.notify(
                payer.getUser(),
                "Boleto pago",
                "Você pagou um boleto de R$ " + request.amount() + " (código: " + request.barcode() + ")"
        );

        return new BoletoPaymentResponseDTO(
                boleto.getBarcode(),
                boleto.getAmount(),
                boleto.getPaymentDate(),
                boleto.getStatus(),
                boleto.getDescription()
        );
    }

    public List<TransactionStatementResponseDTO> getStatement(String email) {
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