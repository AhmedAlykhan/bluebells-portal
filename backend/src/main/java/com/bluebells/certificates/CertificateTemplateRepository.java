package com.bluebells.certificates;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateTemplateRepository extends JpaRepository<CertificateTemplate, Long> {

    List<CertificateTemplate> findByActiveTrue();

    Optional<CertificateTemplate> findByCertificateTypeAndActiveTrue(String certificateType);
}
