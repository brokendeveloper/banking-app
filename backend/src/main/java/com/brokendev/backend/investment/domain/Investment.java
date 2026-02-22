package com.brokendev.backend.investment.domain;

import com.brokendev.backend.account.domain.Account;
import com.brokendev.backend.enums.InvestmentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "investments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Account investor;

    @Enumerated(EnumType.STRING)
    private InvestmentType type;

    private BigDecimal amount;

    private LocalDateTime investmentDate;

    private BigDecimal expectedReturn;

    private LocalDateTime maturityDate;

    private boolean redeemed;

}
