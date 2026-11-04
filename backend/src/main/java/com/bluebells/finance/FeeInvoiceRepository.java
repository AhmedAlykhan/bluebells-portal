package com.bluebells.finance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeeInvoiceRepository extends JpaRepository<FeeInvoice, Long> {
    Optional<FeeInvoice> findByInvoiceNumber(String invoiceNumber);
    boolean existsByInvoiceNumber(String invoiceNumber);
    List<FeeInvoice> findByStudentId(Long studentId);
    List<FeeInvoice> findByStudentIdOrderByDueDateDesc(Long studentId);
    List<FeeInvoice> findByStudentIdAndStatusInOrderByDueDateAsc(Long studentId, List<String> statuses);
    Optional<FeeInvoice> findByStudentIdAndAcademicSessionIdAndBillingMonth(Long studentId, Long sessionId, String billingMonth);
}
