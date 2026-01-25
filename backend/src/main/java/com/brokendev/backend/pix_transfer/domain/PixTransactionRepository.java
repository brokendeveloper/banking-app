package com.brokendev.backend.pix_transfer.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PixTransactionRepository extends JpaRepository<PixTransaction, Long> {

}
