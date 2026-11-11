package com.bluebells.certificates;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IssuedCertificateRepository extends JpaRepository<IssuedCertificate, Long> {

    List<IssuedCertificate> findByStudentIdOrderByIssueDateDesc(Long studentId);

    Optional<IssuedCertificate> findByVerificationCode(String verificationCode);

    Optional<IssuedCertificate> findByCertificateNumber(String certificateNumber);

    List<IssuedCertificate> findByCertificateType(String certificateType);

    List<IssuedCertificate> findByStatus(String status);
}
