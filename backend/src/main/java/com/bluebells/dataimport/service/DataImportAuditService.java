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
    private final com.bluebells.identity.UserRepository userRepository;

    public DataImportAuditService(DataImportAuditLogRepository auditLogRepository, com.bluebells.identity.UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void logAudit(DataImportJob job, String action, String status, String details, String ipAddress, String userAgent) {
        User user = job != null ? job.getUploadedBy() : null;
        logAudit(job, user, action, status, details, ipAddress, userAgent);
    }

    @Transactional
    public void logAudit(DataImportJob job, User user, String action, String status, String details, String ipAddress, String userAgent) {
        if (user == null && job != null) {
            user = job.getUploadedBy();
        }
        if (user == null) {
            user = userRepository.findByUsername("admin").orElse(null);
        }
        if (user != null) {
            DataImportAuditLog log = new DataImportAuditLog(job, user, action, ipAddress, details);
            auditLogRepository.save(log);
        }
    }

    @Transactional
    public void log(DataImportJob job, User user, String action, String ipAddress, String details) {
        logAudit(job, user, action, "SUCCESS", details, ipAddress, null);
    }
}
