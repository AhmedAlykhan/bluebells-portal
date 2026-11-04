-- ============================================================================
-- V5__bulk_data_import_schema.sql
-- Master Data Import Module Schema & Permissions
-- Bluebells Schooling System, Hyderabad
-- ============================================================================

-- 1. Create New Data Import Permissions
INSERT IGNORE INTO permissions (id, code, module, description) VALUES
(17, 'DATA_IMPORT_VIEW', 'DATA_IMPORT', 'View bulk data import module, dashboard, and metrics'),
(18, 'DATA_IMPORT_DOWNLOAD_TEMPLATE', 'DATA_IMPORT', 'Download student and staff Excel/CSV import templates'),
(19, 'DATA_IMPORT_UPLOAD', 'DATA_IMPORT', 'Upload student and staff data files for staging and processing'),
(20, 'DATA_IMPORT_VALIDATE', 'DATA_IMPORT', 'Validate staged data files against school database and constraints'),
(21, 'DATA_IMPORT_CONFIRM', 'DATA_IMPORT', 'Confirm and commit validated student and staff records into database'),
(22, 'DATA_IMPORT_VIEW_HISTORY', 'DATA_IMPORT', 'Inspect import job logs, audit trails, and execution summaries'),
(23, 'DATA_IMPORT_DOWNLOAD_REPORT', 'DATA_IMPORT', 'Download detailed error logs, success summaries, and validation reports'),
(24, 'DATA_IMPORT_CANCEL', 'DATA_IMPORT', 'Cancel pending or staged data import jobs');

-- 2. Grant Import Permissions to Super Admin (Role 1)
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
(1, 17), (1, 18), (1, 19), (1, 20), (1, 21), (1, 22), (1, 23), (1, 24);

-- 3. Grant Default Import Permissions to Principal (Role 3)
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
(3, 17), (3, 18), (3, 19), (3, 20), (3, 21), (3, 22), (3, 23);

-- 4. Create Table: data_import_jobs
CREATE TABLE IF NOT EXISTS data_import_jobs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id VARCHAR(64) NOT NULL UNIQUE,
    import_type VARCHAR(32) NOT NULL, -- STUDENT, STAFF
    original_filename VARCHAR(255) NOT NULL,
    file_format VARCHAR(16) NOT NULL, -- XLSX, CSV
    file_size_bytes BIGINT NOT NULL,
    file_path VARCHAR(512) NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'UPLOADED', -- UPLOADED, VALIDATING, VALIDATED, CONFIRMED, PROCESSING, COMPLETED, COMPLETED_WITH_ERRORS, FAILED, CANCELLED
    import_mode VARCHAR(32) NOT NULL DEFAULT 'STRICT', -- STRICT, VALID_ONLY
    total_rows INT NOT NULL DEFAULT 0,
    valid_rows INT NOT NULL DEFAULT 0,
    invalid_rows INT NOT NULL DEFAULT 0,
    duplicate_rows INT NOT NULL DEFAULT 0,
    warning_rows INT NOT NULL DEFAULT 0,
    imported_rows INT NOT NULL DEFAULT 0,
    skipped_rows INT NOT NULL DEFAULT 0,
    uploaded_by_id BIGINT NOT NULL,
    confirmed_by_id BIGINT NULL,
    summary_notes TEXT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    completed_at DATETIME(6) NULL,
    INDEX idx_import_jobs_type (import_type),
    INDEX idx_import_jobs_status (status),
    INDEX idx_import_jobs_created_at (created_at),
    CONSTRAINT fk_import_jobs_uploader FOREIGN KEY (uploaded_by_id) REFERENCES users(id),
    CONSTRAINT fk_import_jobs_confirmer FOREIGN KEY (confirmed_by_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Create Table: data_import_job_rows
CREATE TABLE IF NOT EXISTS data_import_job_rows (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id BIGINT NOT NULL,
    `row_number` INT NOT NULL,
    sheet_name VARCHAR(64) NOT NULL DEFAULT 'Main',
    entity_identifier VARCHAR(64) NULL, -- admission_number or employee_id
    row_data_json MEDIUMTEXT NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'VALID', -- VALID, INVALID, DUPLICATE, WARNING, IMPORTED, SKIPPED, FAILED
    error_message TEXT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX idx_import_rows_job_status (job_id, status),
    INDEX idx_import_rows_identifier (entity_identifier),
    CONSTRAINT fk_import_rows_job FOREIGN KEY (job_id) REFERENCES data_import_jobs(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Create Table: data_import_errors
CREATE TABLE IF NOT EXISTS data_import_errors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id BIGINT NOT NULL,
    `row_number` INT NOT NULL,
    sheet_name VARCHAR(64) NOT NULL DEFAULT 'Main',
    field_name VARCHAR(64) NOT NULL,
    field_value VARCHAR(255) NULL,
    error_code VARCHAR(64) NOT NULL,
    error_message VARCHAR(512) NOT NULL,
    severity VARCHAR(16) NOT NULL DEFAULT 'ERROR', -- ERROR, WARNING
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX idx_import_errors_job_row (job_id, `row_number`),
    CONSTRAINT fk_import_errors_job FOREIGN KEY (job_id) REFERENCES data_import_jobs(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Create Table: data_import_audit_logs
CREATE TABLE IF NOT EXISTS data_import_audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id BIGINT NULL,
    user_id BIGINT NOT NULL,
    action VARCHAR(64) NOT NULL, -- UPLOAD, VALIDATE, CONFIRM, CANCEL, DOWNLOAD_TEMPLATE, DOWNLOAD_REPORT, VIEW_PREVIEW
    ip_address VARCHAR(64) NULL,
    details TEXT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX idx_import_audit_job (job_id),
    INDEX idx_import_audit_user (user_id),
    INDEX idx_import_audit_action (action),
    CONSTRAINT fk_import_audit_job FOREIGN KEY (job_id) REFERENCES data_import_jobs(id) ON DELETE SET NULL,
    CONSTRAINT fk_import_audit_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
