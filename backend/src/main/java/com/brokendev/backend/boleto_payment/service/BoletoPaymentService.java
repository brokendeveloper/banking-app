package com.brokendev.backend.boleto_payment.service;

import com.brokendev.backend.account.domain.Account;
import com.brokendev.backend.account.domain.AccountRepository;
import com.brokendev.backend.boleto_payment.domain.BoletoPayment;
import com.brokendev.backend.boleto_payment.domain.BoletoPaymentRepository;
import com.brokendev.backend.boleto_payment.dto.BoletoPaymentRequestDTO;
import com.brokendev.backend.boleto_payment.dto.BoletoPaymentResponseDTO;
import com.brokendev.backend.common.exceptions.InvalidBoletoAmountException;
import com.brokendev.backend.common.exceptions.UserAccountNotFoundException;
import com.brokendev.backend.common.exceptions.InsufficientBalanceException;
import com.brokendev.backend.common.enums.BoletoPaymentStatus;
import com.brokendev.backend.notification.service.NotificationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class BoletoPaymentService {

    private final BoletoPaymentRepository boletoPaymentRepository;

    private final AccountRepository accountRepository;

    private final NotificationService notificationService;


    public BoletoPaymentService(BoletoPaymentRepository boletoPaymentRepository, AccountRepository accountRepository, NotificationService notificationService) {
        this.boletoPaymentRepository = boletoPaymentRepository;
        this.accountRepository = accountRepository;
        this.notificationService = notificationService;
    }

    public BoletoPaymentResponseDTO payBoleto(String payerEmail, BoletoPaymentRequestDTO request) {
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidBoletoAmountException("The amount must be greater than zero");
        }
        Account payer = accountRepository.findByUserEmail(payerEmail)
                .orElseThrow(() -> new UserAccountNotFoundException("Could not find account with email " + payerEmail));

        if(payer.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientBalanceException("Amount not enough to pay boleto");
        }

        payer.setBalance(payer.getBalance().subtract(request.amount()));
        accountRepository.save(payer);

        BoletoPayment boleto = new BoletoPayment();
        boleto.setPayer(payer);
        boleto.setBarcode(request.barcode());
        boleto.setAmount(request.amount());
        boleto.setPaymentDate(LocalDateTime.now());
        boleto.setStatus(BoletoPaymentStatus.PAID);
        boleto.setDescription("Payment successful!");
        boletoPaymentRepository.save(boleto);

        // Notificação
        notificationService.notify(
                payer.getUser(),
                "Boleto payment successful!",
                "You paid a R$ " + request.amount() + " (code: " + request.barcode() + " )"
        );

        return new BoletoPaymentResponseDTO(
                boleto.getBarcode(),
                boleto.getAmount(),
                boleto.getPaymentDate(),
                boleto.getStatus(),
                boleto.getDescription()
        );
    }
}
