package com.brokendev.backend.investment.service;

import com.brokendev.backend.common.exceptions.*;
import com.brokendev.backend.account.domain.Account;
import com.brokendev.backend.investment.domain.Investment;
import com.brokendev.backend.investment.dto.InvestmentRequestDTO;
import com.brokendev.backend.investment.dto.InvestmentResponseDTO;
import com.brokendev.backend.common.enums.InvestmentType;
import com.brokendev.backend.account.domain.AccountRepository;
import com.brokendev.backend.investment.domain.InvestmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class InvestmentService {

    private final InvestmentRepository investmentRepository;

    private final AccountRepository accountRepository;

    public InvestmentService(InvestmentRepository investmentRepository, AccountRepository accountRepository){
        this.investmentRepository = investmentRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public InvestmentResponseDTO invest(String userEmail, InvestmentRequestDTO request) {
        Account investor = accountRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new UserAccountNotFoundException("Account not found"));
        if(investor.getBalance().compareTo(request.amount()) < 0){
            throw new InsufficientBalanceException("Insufficient balance to invest");
        }


        investor.setBalance(investor.getBalance().subtract(request.amount()));
        accountRepository.save(investor);


        BigDecimal expectedReturn = calcularRetorno(request.type(), request.amount());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime maturityDate = now.plus(30, ChronoUnit.DAYS);

        Investment investment = new Investment();
        investment.setInvestor(investor);
        investment.setType(request.type());
        investment.setAmount(request.amount());
        investment.setInvestmentDate(now);
        investment.setExpectedReturn(expectedReturn);
        investment.setMaturityDate(maturityDate);
        investment.setRedeemed(false);

        investmentRepository.save(investment);

        return new InvestmentResponseDTO(
                investment.getId(),
                investment.getType(),
                investment.getAmount(),
                investment.getInvestmentDate(),
                investment.getExpectedReturn(),
                investment.getMaturityDate(),
                investment.isRedeemed()
        );
    }

    public List<InvestmentResponseDTO> listInvestments(String userEmail) {
        Account investor = accountRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new UserAccountNotFoundException("Account not found"));
        return investmentRepository.findByInvestor(investor)
                .stream()
                .map(inv -> new InvestmentResponseDTO(
                        inv.getId(),
                        inv.getType(),
                        inv.getAmount(),
                        inv.getInvestmentDate(),
                        inv.getExpectedReturn(),
                        inv.getMaturityDate(),
                        inv.isRedeemed()
                ))
                .toList();
    }

    @Transactional
    public InvestmentResponseDTO redeemInvestment(String userEmail, Long investmentId) {
        Account investor = accountRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new UserAccountNotFoundException("Account not found"));

        Investment investment = investmentRepository.findById(investmentId)
                .orElseThrow(() -> new InvestmentNotFoundException("Investment not found"));

        if (!investment.getInvestor().getId().equals(investor.getId())) {
            throw new InvestmentOwnershipException("The investment does not belong to the user.");
        }
        if (investment.isRedeemed()) {
            throw new InvestmentAlreadyRedeemedException("The investment has already been recovered.");
        }
        if (LocalDateTime.now().isBefore(investment.getMaturityDate())) {
                throw new InvestmentNotMaturedException("Investment has not yet matured");
        }


        investor.setBalance(investor.getBalance().add(investment.getExpectedReturn()));
        accountRepository.save(investor);


        investment.setRedeemed(true);
        investmentRepository.save(investment);

        return new InvestmentResponseDTO(
                investment.getId(),
                investment.getType(),
                investment.getAmount(),
                investment.getInvestmentDate(),
                investment.getExpectedReturn(),
                investment.getMaturityDate(),
                investment.isRedeemed()
        );
    }


    private BigDecimal calcularRetorno(InvestmentType type, BigDecimal amount) {
        double taxa = switch (type) {
            case CDB -> 1.10;
            case TESOURO_DIRETO -> 1.12;
            case LCI -> 1.09;
            case LCA -> 1.08;
            case POUPANCA -> 1.06;
        };
        return amount.multiply(BigDecimal.valueOf(taxa));
    }
}
