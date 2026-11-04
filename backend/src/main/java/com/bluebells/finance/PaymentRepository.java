package com.bluebells.finance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentReference(String paymentReference);
    boolean existsByPaymentReference(String paymentReference);
    List<Payment> findByStudentIdOrderByPaymentDateDesc(Long studentId);
}
