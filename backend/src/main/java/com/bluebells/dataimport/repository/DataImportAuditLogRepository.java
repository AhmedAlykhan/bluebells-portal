package com.bluebells.dataimport.repository;

import com.bluebells.dataimport.entity.DataImportAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataImportAuditLogRepository extends JpaRepository<DataImportAuditLog, Long> {

    List<DataImportAuditLog> findByJobIdOrderByCreatedAtDesc(Long jobId);

    Page<DataImportAuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
