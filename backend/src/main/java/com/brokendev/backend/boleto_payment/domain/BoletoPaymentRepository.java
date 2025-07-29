package com.brokendev.backend.boleto_payment.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BoletoPaymentRepository extends JpaRepository<BoletoPayment, Long> {
}
