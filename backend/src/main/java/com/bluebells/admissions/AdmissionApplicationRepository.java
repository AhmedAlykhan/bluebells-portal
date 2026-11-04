package com.bluebells.admissions;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdmissionApplicationRepository extends JpaRepository<AdmissionApplication, Long> {
    Optional<AdmissionApplication> findByApplicationNumber(String applicationNumber);
    Page<AdmissionApplication> findByStatus(String status, Pageable pageable);
    boolean existsByApplicationNumber(String applicationNumber);
}
