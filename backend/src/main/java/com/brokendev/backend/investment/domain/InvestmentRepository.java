package com.brokendev.backend.investment.domain;

import com.brokendev.backend.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvestmentRepository extends JpaRepository<Investment, Long> {

    List<Investment> findByInvestor(Account investor);
}
