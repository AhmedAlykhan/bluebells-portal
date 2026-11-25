-- ============================================================================
-- V9__batch3_database_hardening.sql
-- Batch 3: Database hardening, constraints, vice principal role, and DATETIME(6)
-- ============================================================================

-- 1. Seed ROLE_VICE_PRINCIPAL
INSERT IGNORE INTO roles (id, name, description) VALUES
(8, 'ROLE_VICE_PRINCIPAL', 'Academic Vice Principal with academic, attendance, and exam oversight');

-- Map permissions for ROLE_VICE_PRINCIPAL:
-- ACADEMICS_MANAGE (3), STUDENT_MANAGE (5), ATTENDANCE_RECORD (6), ATTENDANCE_CORRECT (7),
-- LMS_MANAGE (8), EXAM_MANAGE (10), MARKS_ENTRY (11), RESULTS_PUBLISH (12), REPORTS_VIEW (15)
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
(8, 3), (8, 5), (8, 6), (8, 7), (8, 8), (8, 10), (8, 11), (8, 12), (8, 15);

-- 2. Standardize DATETIME(6) across V7 tables for uniform microsecond precision
ALTER TABLE certificate_templates 
    MODIFY COLUMN created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    MODIFY COLUMN updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);

ALTER TABLE issued_certificates 
    MODIFY COLUMN created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    MODIFY COLUMN updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);

ALTER TABLE school_integrations 
    MODIFY COLUMN updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);

ALTER TABLE student_development_records 
    MODIFY COLUMN created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    MODIFY COLUMN updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);

ALTER TABLE student_id_cards 
    MODIFY COLUMN created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    MODIFY COLUMN updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);

ALTER TABLE support_ticket_messages 
    MODIFY COLUMN created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6);

ALTER TABLE support_tickets 
    MODIFY COLUMN created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    MODIFY COLUMN updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);

-- 3. Unique Constraints for Business Integrity
ALTER TABLE fee_invoices 
    ADD CONSTRAINT uq_invoice_student_session_month UNIQUE (student_id, session_id, billing_month);

ALTER TABLE courses 
    ADD CONSTRAINT uq_course_subject_section UNIQUE (subject_id, section_id);

-- 4. Check Constraints for Financial & Academic Guardrails
ALTER TABLE fee_invoices 
    ADD CONSTRAINT chk_invoice_balance CHECK (balance_due >= 0.00),
    ADD CONSTRAINT chk_invoice_paid CHECK (paid_amount >= 0.00),
    ADD CONSTRAINT chk_invoice_total CHECK (total_amount >= 0.00);

ALTER TABLE payments 
    ADD CONSTRAINT chk_payment_amount CHECK (amount > 0.00);

ALTER TABLE marks_entries 
    ADD CONSTRAINT chk_marks_obtained CHECK (marks_obtained >= 0.00),
    ADD CONSTRAINT chk_max_marks CHECK (max_marks > 0.00);

ALTER TABLE terms 
    ADD CONSTRAINT chk_term_weight CHECK (weight_percentage >= 0.00 AND weight_percentage <= 100.00);
