-- ====================================================================
-- Flyway Migration V7: Advanced Analytics, Student Development,
-- Digital Student IDs, Certificates, Support Centre & Integrations
-- ====================================================================

-- 1. Student Development Records
CREATE TABLE IF NOT EXISTS student_development_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    academic_session_id BIGINT NOT NULL,
    category VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    event_name VARCHAR(255) NULL,
    achievement_date DATE NOT NULL,
    `level` VARCHAR(50) NULL,
    position_or_award VARCHAR(100) NULL,
    evidence_file_path VARCHAR(500) NULL,
    evidence_file_name VARCHAR(255) NULL,
    recorded_by BIGINT NOT NULL,
    teacher_appreciation_note TEXT NULL,
    skills_acquired VARCHAR(500) NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'VERIFIED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_sdr_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_sdr_session FOREIGN KEY (academic_session_id) REFERENCES academic_sessions(id),
    CONSTRAINT fk_sdr_user FOREIGN KEY (recorded_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Student ID Cards
CREATE TABLE IF NOT EXISTS student_id_cards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    academic_session_id BIGINT NOT NULL,
    card_number VARCHAR(50) NOT NULL UNIQUE,
    issue_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    reissue_count INT NOT NULL DEFAULT 0,
    reissue_reason VARCHAR(255) NULL,
    reissued_at TIMESTAMP NULL,
    reissued_by BIGINT NULL,
    qr_verification_token VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_sic_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_sic_session FOREIGN KEY (academic_session_id) REFERENCES academic_sessions(id),
    CONSTRAINT fk_sic_reissued_by FOREIGN KEY (reissued_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Digital Certificate Templates
CREATE TABLE IF NOT EXISTS certificate_templates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    certificate_type VARCHAR(50) NOT NULL,
    header_text VARCHAR(255) NOT NULL,
    body_template TEXT NOT NULL,
    default_signatory_title VARCHAR(100) NOT NULL DEFAULT 'Principal',
    default_signatory_name VARCHAR(100) NOT NULL DEFAULT 'Huma Arif',
    border_style VARCHAR(50) NOT NULL DEFAULT 'ROYAL_GOLD',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Issued Certificates
CREATE TABLE IF NOT EXISTS issued_certificates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    certificate_number VARCHAR(50) NOT NULL UNIQUE,
    verification_code VARCHAR(64) NOT NULL UNIQUE,
    template_id BIGINT NULL,
    student_id BIGINT NOT NULL,
    certificate_type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    recipient_name VARCHAR(255) NOT NULL,
    admission_number VARCHAR(50) NOT NULL,
    class_name VARCHAR(100) NOT NULL,
    section_name VARCHAR(50) NOT NULL,
    academic_session VARCHAR(100) NOT NULL,
    narrative TEXT NOT NULL,
    issue_date DATE NOT NULL,
    issuing_authority_name VARCHAR(100) NOT NULL,
    issuing_authority_title VARCHAR(100) NOT NULL,
    issued_by BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ISSUED',
    revocation_reason VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ic_template FOREIGN KEY (template_id) REFERENCES certificate_templates(id) ON DELETE SET NULL,
    CONSTRAINT fk_ic_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_ic_issuer FOREIGN KEY (issued_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Support & Complaint Tickets
CREATE TABLE IF NOT EXISTS support_tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticket_number VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    student_id BIGINT NULL,
    category VARCHAR(50) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    assigned_department VARCHAR(50) NOT NULL DEFAULT 'ADMINISTRATION',
    assigned_to BIGINT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    attachment_url VARCHAR(500) NULL,
    attachment_name VARCHAR(255) NULL,
    resolution_notes TEXT NULL,
    resolved_at TIMESTAMP NULL,
    resolved_by BIGINT NULL,
    escalation_level INT NOT NULL DEFAULT 0,
    escalation_notes TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_st_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_st_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE SET NULL,
    CONSTRAINT fk_st_assigned_to FOREIGN KEY (assigned_to) REFERENCES users(id),
    CONSTRAINT fk_st_resolved_by FOREIGN KEY (resolved_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Support Ticket Messages / Conversation History
CREATE TABLE IF NOT EXISTS support_ticket_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticket_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    sender_name VARCHAR(100) NOT NULL,
    sender_role VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    attachment_url VARCHAR(500) NULL,
    attachment_name VARCHAR(255) NULL,
    is_internal_note BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_stm_ticket FOREIGN KEY (ticket_id) REFERENCES support_tickets(id) ON DELETE CASCADE,
    CONSTRAINT fk_stm_sender FOREIGN KEY (sender_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. School Integrations Registry
CREATE TABLE IF NOT EXISTS school_integrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    integration_key VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'READY_FOR_CREDENTIALS',
    config_schema TEXT NULL,
    config_values TEXT NULL,
    is_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    last_heartbeat TIMESTAMP NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. Seed New Permissions
INSERT INTO permissions (id, code, module, description) VALUES
(25, 'ANALYTICS_VIEW', 'ANALYTICS', 'View management intelligence and reporting dashboards'),
(26, 'ANALYTICS_EXPORT', 'ANALYTICS', 'Export institutional analytics reports to PDF and Excel'),
(27, 'STUDENT_DEVELOPMENT_VIEW', 'DEVELOPMENT', 'View student development profiles and co-curricular achievements'),
(28, 'STUDENT_DEVELOPMENT_CREATE', 'DEVELOPMENT', 'Record new student achievements, sports awards, and milestones'),
(29, 'STUDENT_DEVELOPMENT_EDIT', 'DEVELOPMENT', 'Edit and verify student development records'),
(30, 'STUDENT_ID_VIEW', 'STUDENT_ID', 'View and download student digital ID cards'),
(31, 'STUDENT_ID_GENERATE', 'STUDENT_ID', 'Generate, batch print, and reissue student ID cards'),
(32, 'CERTIFICATES_VIEW', 'CERTIFICATES', 'View and verify digital student certificates'),
(33, 'CERTIFICATES_MANAGE', 'CERTIFICATES', 'Create certificate templates and issue certificates'),
(34, 'SUPPORT_TICKETS_VIEW', 'SUPPORT', 'View support tickets (parents view own, staff view assigned)'),
(35, 'SUPPORT_TICKETS_MANAGE', 'SUPPORT', 'Manage, reply to, resolve and escalate support tickets'),
(36, 'INTEGRATIONS_MANAGE', 'INTEGRATIONS', 'Configure and monitor external communication and payment integrations')
ON DUPLICATE KEY UPDATE description=VALUES(description);

-- 9. Assign Permissions to Roles
-- Super Admin (Role 1): Full permissions
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
(1, 25), (1, 26), (1, 27), (1, 28), (1, 29), (1, 30), (1, 31), (1, 32), (1, 33), (1, 34), (1, 35), (1, 36);

-- School Admin (Role 2)
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
(2, 25), (2, 26), (2, 27), (2, 28), (2, 29), (2, 30), (2, 31), (2, 32), (2, 33), (2, 34), (2, 35), (2, 36);

-- Principal Huma Arif (Role 3)
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
(3, 25), (3, 26), (3, 27), (3, 28), (3, 29), (3, 30), (3, 31), (3, 32), (3, 33), (3, 34), (3, 35), (3, 36);

-- Teachers (Role 4): Development record/view, ID view, certificates view, support view/reply
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
(4, 27), (4, 28), (4, 30), (4, 32), (4, 34);

-- Parents (Role 5): Read-only development, ward ID, ward certificates, own tickets
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
(5, 27), (5, 30), (5, 32), (5, 34);

-- Students (Role 6): Read-only development, own ID, own certificates, own tickets
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
(6, 27), (6, 30), (6, 32), (6, 34);

-- Accountant (Role 7): Analytics view & export, support tickets
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
(7, 25), (7, 26), (7, 34);

-- 10. Seed Baseline Certificate Templates
INSERT INTO certificate_templates (id, title, certificate_type, header_text, body_template, default_signatory_title, default_signatory_name, border_style, is_active) VALUES
(1, 'Certificate of Academic Excellence', 'MERIT', 'BLUEBELLS SCHOOLING SYSTEM - ACADEMIC HONORS',
 'This is to proudly certify that {student_name}, son/daughter of {guardian_name}, bearing Admission No. {admission_number}, of {class_name} - {section_name}, has demonstrated outstanding scholastic performance and achieved High Distinction in the Academic Session {academic_session}.',
 'Academic Principal', 'Huma Arif', 'ROYAL_GOLD', 1),
(2, 'Annual Sports Championship Award', 'SPORTS', 'BLUEBELLS SCHOOLING SYSTEM - ATHLETIC EXCELLENCE',
 'This certificate is awarded to {student_name} of {class_name} - {section_name} for exceptional athletic prowess, sportsmanship, and outstanding dedication during the Annual Inter-House Sports Championship, Session {academic_session}.',
 'Vice Principal', 'Hana Khan', 'EMERALD_HONOR', 1),
(3, 'Co-Curricular & Debate Distinction', 'ACHIEVEMENT', 'BLUEBELLS SCHOOLING SYSTEM - TALENT RECOGNITION',
 'Presented to {student_name} in recognition of exemplary eloquence, critical reasoning, and valuable representation in the Inter-School Bilingual Declamation & Literary Competition during Academic Session {academic_session}.',
 'Academic Principal', 'Huma Arif', 'CLASSIC_NAVY', 1),
(4, 'Certificate of Good Conduct & Character', 'CONDUCT', 'BLUEBELLS SCHOOLING SYSTEM - INSTITUTIONAL RECOGNITION',
 'This certifies that {student_name}, bearing Admission No. {admission_number}, has maintained an exemplary disciplinary record, demonstrated commendable moral integrity, and upheld school values during Academic Session {academic_session}.',
 'Academic Principal', 'Huma Arif', 'ROYAL_GOLD', 1),
(5, 'Course Completion Certificate', 'COMPLETION', 'BLUEBELLS SCHOOLING SYSTEM - CURRICULAR ATTAINMENT',
 'This is to certify that {student_name} has successfully fulfilled all prescribed academic, laboratory, and coursework requirements for {class_name} during the Academic Session {academic_session}.',
 'Academic Principal', 'Huma Arif', 'CLASSIC_NAVY', 1)
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- 11. Seed Baseline Integrations Registry
INSERT INTO school_integrations (id, integration_key, name, category, description, status, is_enabled) VALUES
(1, 'PUSH_NOTIFICATIONS', 'Web & Mobile Push Notifications', 'COMMUNICATION', 'Firebase Cloud Messaging (FCM) integration for real-time announcements, attendance alerts, and fee updates.', 'READY_FOR_CREDENTIALS', 0),
(2, 'SMS_GATEWAY', 'Pakistan Telco SMS Gateway', 'COMMUNICATION', 'Direct telco SMS gateway (Jazz, Telenor, Zong, Ufone) for instant emergency broadcasts and fee challan delivery.', 'READY_FOR_CREDENTIALS', 0),
(3, 'WHATSAPP_BUSINESS', 'Official WhatsApp Business API', 'COMMUNICATION', 'Automated fee vouchers, attendance alerts, and verified school announcements sent directly to parents on WhatsApp.', 'READY_FOR_CREDENTIALS', 0),
(4, 'ONLINE_FEE_PAYMENT', '1Link & Digital Wallets Gateway', 'FINANCE', 'Direct online tuition fee payments via 1Link 1Bill, KuickPay, JazzCash, EasyPaisa, and Visa/Mastercard.', 'READY_FOR_CREDENTIALS', 0),
(5, 'GPS_TRANSPORT', 'Campus Transport Fleet GPS Tracker', 'TRANSPORT', 'Real-time vehicle telemetry and bus location tracking for school vans and buses in Hyderabad and Latifabad.', 'READY_FOR_CREDENTIALS', 0),
(6, 'QR_ATTENDANCE', 'Campus Turnstile QR Scanner Integration', 'ATTENDANCE', 'Hardware turnstile and handheld RFID/QR scanner integration for automated student and staff entrance logging.', 'READY_FOR_CREDENTIALS', 0),
(7, 'EXTERNAL_CALENDAR', 'Google Calendar & iCal Synchronization', 'CALENDAR', 'Automated synchronization of academic terms, examination timetables, and school holidays with Google Calendar.', 'CONFIGURED_OPERATIONAL', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 12. Seed Sample Student Development Records (Safe conditional insert based on student existence)
INSERT INTO student_development_records (student_id, academic_session_id, category, title, description, event_name, achievement_date, `level`, position_or_award, recorded_by, teacher_appreciation_note, skills_acquired, status)
SELECT s.id, a.id, 'ACADEMIC_ACHIEVEMENT', 'First Position in Science & Robotics Exhibition', 'Designed an automated irrigation system using Arduino and soil moisture sensors.', 'Annual Hyderabad District STEM Fair', '2026-09-15', 'DISTRICT', '1st Position (Gold Medal)', COALESCE(u.id, 1), 'Ali displayed extraordinary technical depth and problem-solving abilities.', 'Embedded C, Robotics, Circuit Design', 'VERIFIED'
FROM students s
CROSS JOIN academic_sessions a
LEFT JOIN users u ON u.username = 'teacher.ahmed'
WHERE s.admission_number = 'BSS-2026-0001' AND a.is_current = 1
LIMIT 1;

INSERT INTO student_development_records (student_id, academic_session_id, category, title, description, event_name, achievement_date, `level`, position_or_award, recorded_by, teacher_appreciation_note, skills_acquired, status)
SELECT s.id, a.id, 'SPORTS', 'Cricket Team Captaincy & Best All-Rounder', 'Led Class 10 cricket team to victory in the Inter-School T20 Championship.', 'BISE Hyderabad Inter-School Cricket Cup', '2026-10-04', 'DIVISION', 'Champions Trophy & Best All-Rounder', COALESCE(u.id, 1), 'Superb leadership on the pitch and excellent athletic discipline.', 'Leadership, Teamwork, Sportsmanship', 'VERIFIED'
FROM students s
CROSS JOIN academic_sessions a
LEFT JOIN users u ON u.username = 'teacher.ahmed'
WHERE s.admission_number = 'BSS-2026-0001' AND a.is_current = 1
LIMIT 1;

INSERT INTO student_development_records (student_id, academic_session_id, category, title, description, event_name, achievement_date, `level`, position_or_award, recorded_by, teacher_appreciation_note, skills_acquired, status)
SELECT s.id, a.id, 'CO_CURRICULAR', 'English Declamation Contest Winner', 'Delivered an impassioned keynote speech on AI in Modern Education.', 'Inter-Campus Debate Championship', '2026-10-20', 'INTER_SCHOOL', '1st Runner-Up', COALESCE(u.id, 1), 'Eloquent orator with commanding stage presence.', 'Public Speaking, Rhetoric, Critical Analysis', 'VERIFIED'
FROM students s
CROSS JOIN academic_sessions a
LEFT JOIN users u ON u.username = 'teacher.fatima'
WHERE s.admission_number = 'BSS-2026-0001' AND a.is_current = 1
LIMIT 1;

INSERT INTO student_development_records (student_id, academic_session_id, category, title, description, event_name, achievement_date, `level`, position_or_award, recorded_by, teacher_appreciation_note, skills_acquired, status)
SELECT s.id, a.id, 'ACADEMIC_ACHIEVEMENT', 'High Distinction in Mathematics Olympiad', 'Scored in the 99th percentile across Sindh province in advanced algebra and geometry.', 'Sindh Mathematical Society Olympiad', '2026-09-22', 'PROVINCIAL', 'Distinction Certificate & Shield', COALESCE(u.id, 1), 'Sara has exceptional mathematical intuition and meticulous methodology.', 'Advanced Algebra, Geometry, Mathematical Logic', 'VERIFIED'
FROM students s
CROSS JOIN academic_sessions a
LEFT JOIN users u ON u.username = 'teacher.ahmed'
WHERE s.admission_number = 'BSS-2026-0002' AND a.is_current = 1
LIMIT 1;

-- 13. Seed Baseline Active ID Cards (Conditional based on student existence)
INSERT IGNORE INTO student_id_cards (student_id, academic_session_id, card_number, issue_date, expiry_date, status, reissue_count, qr_verification_token)
SELECT s.id, a.id, CONCAT('IDC-2026-', LPAD(s.id, 4, '0')), '2026-08-01', '2027-06-30', 'ACTIVE', 0, CONCAT('BSS-ID-TOKEN-', s.admission_number, '-2026')
FROM students s
CROSS JOIN academic_sessions a
WHERE a.is_current = 1 AND s.admission_number IN ('BSS-2026-0001', 'BSS-2026-0002');

-- 14. Seed Sample Issued Certificates (Conditional based on student existence)
INSERT IGNORE INTO issued_certificates (certificate_number, verification_code, template_id, student_id, certificate_type, title, recipient_name, admission_number, class_name, section_name, academic_session, narrative, issue_date, issuing_authority_name, issuing_authority_title, issued_by, status)
SELECT 'BSS-CERT-2026-0001', 'VRF-9821-BSS-ALI', 1, s.id, 'MERIT', 'Certificate of Academic Excellence', CONCAT(s.first_name, ' ', s.last_name), s.admission_number, 'Class 10', 'Section A', a.name, 'This is to proudly certify that Ali Khan has demonstrated outstanding scholastic performance and achieved High Distinction in the Academic Session 2026-2027.', '2026-10-30', 'Huma Arif', 'Academic Principal', 1, 'ISSUED'
FROM students s
CROSS JOIN academic_sessions a
WHERE s.admission_number = 'BSS-2026-0001' AND a.is_current = 1
LIMIT 1;

-- 15. Seed Sample Support Tickets (Conditional based on user existence)
INSERT IGNORE INTO support_tickets (id, ticket_number, user_id, student_id, category, subject, description, priority, assigned_department, status)
SELECT 1, 'TKT-2026-0001', u.id, s.id, 'FEE_QUERY', 'Inquiry regarding Sibling Discount adjustment in October Voucher', 'Assalam-o-Alaikum, I noticed that the sibling discount for Ali Khan was not adjusted in the October challan. Kindly verify and update.', 'MEDIUM', 'FINANCE', 'RESOLVED'
FROM users u
LEFT JOIN students s ON s.admission_number = 'BSS-2026-0001'
WHERE u.username = 'parent.khan'
LIMIT 1;

INSERT IGNORE INTO support_tickets (id, ticket_number, user_id, student_id, category, subject, description, priority, assigned_department, status)
SELECT 2, 'TKT-2026-0002', u.id, s.id, 'ACADEMIC', 'Request for Additional Practice Materials in Mathematics Class 9', 'Sara requires additional practice worksheets for quadratic equations ahead of the upcoming term assessment.', 'LOW', 'ACADEMICS', 'IN_PROGRESS'
FROM users u
LEFT JOIN students s ON s.admission_number = 'BSS-2026-0002'
WHERE u.username = 'parent.khan'
LIMIT 1;

INSERT IGNORE INTO support_ticket_messages (ticket_id, sender_id, sender_name, sender_role, message, is_internal_note, created_at)
SELECT 1, u.id, u.full_name, 'ROLE_PARENT', 'Assalam-o-Alaikum, I noticed that the sibling discount for Ali Khan was not adjusted in the October challan. Kindly verify and update.', 0, '2026-10-28 14:20:00'
FROM users u
WHERE u.username = 'parent.khan'
LIMIT 1;
