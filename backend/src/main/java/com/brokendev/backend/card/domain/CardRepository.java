package com.brokendev.backend.card.domain;

import com.brokendev.backend.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card>findByAccount(Account account);
}
