package com.bluebells.idcard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentIdCardRepository extends JpaRepository<StudentIdCard, Long> {

    Optional<StudentIdCard> findByStudentIdAndStatus(Long studentId, String status);

    List<StudentIdCard> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    Optional<StudentIdCard> findByCardNumber(String cardNumber);

    Optional<StudentIdCard> findByQrVerificationToken(String qrVerificationToken);

    boolean existsByStudentIdAndStatus(Long studentId, String status);
}
