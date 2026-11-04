package com.bluebells.dataimport.service;

import com.bluebells.dataimport.entity.DataImportAuditLog;
import com.bluebells.dataimport.entity.DataImportJob;
import com.bluebells.dataimport.repository.DataImportAuditLogRepository;
import com.bluebells.identity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DataImportAuditService {

    private final DataImportAuditLogRepository auditLogRepository;

    public DataImportAuditService(DataImportAuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void logAudit(DataImportJob job, String action, String status, String details, String ipAddress, String userAgent) {
        User user = job != null ? job.getUploadedBy() : null;
        DataImportAuditLog log = new DataImportAuditLog(job, user, action, ipAddress, details);
        auditLogRepository.save(log);
    }

    @Transactional
    public void log(DataImportJob job, User user, String action, String ipAddress, String details) {
        DataImportAuditLog log = new DataImportAuditLog(job, user, action, ipAddress, details);
        auditLogRepository.save(log);
    }
}
