-- ============================================================================
-- V2__seed_baseline_data.sql
-- Baseline Master Data for Bluebells Schooling System
-- ============================================================================

-- 1. Roles
INSERT IGNORE INTO roles (id, name, description) VALUES
(1, 'ROLE_SUPER_ADMIN', 'Platform Administrator with unrestricted system access'),
(2, 'ROLE_SCHOOL_ADMIN', 'School Operations & Administrative Officer'),
(3, 'ROLE_PRINCIPAL', 'Academic Principal with oversight and approval authority'),
(4, 'ROLE_TEACHER', 'Class and Subject Instructor'),
(5, 'ROLE_PARENT', 'Guardian / Parent of enrolled students'),
(6, 'ROLE_STUDENT', 'Enrolled Student'),
(7, 'ROLE_ACCOUNTANT', 'Financial and Accounts Officer');

-- 2. Master Permissions
INSERT IGNORE INTO permissions (id, code, module, description) VALUES
(1, 'USER_MANAGE', 'IDENTITY', 'Create, update, and manage system user accounts'),
(2, 'STAFF_MANAGE', 'STAFF', 'Manage staff profiles and teacher assignments'),
(3, 'ACADEMICS_MANAGE', 'ACADEMICS', 'Configure sessions, terms, classes, sections, and subjects'),
(4, 'ADMISSION_MANAGE', 'ADMISSIONS', 'Process admission inquiries and enrollment applications'),
(5, 'STUDENT_MANAGE', 'STUDENTS', 'Manage student records, guardian links, and student status'),
(6, 'ATTENDANCE_RECORD', 'ATTENDANCE', 'Record daily student attendance'),
(7, 'ATTENDANCE_CORRECT', 'ATTENDANCE', 'Submit and approve attendance corrections'),
(8, 'LMS_MANAGE', 'LMS', 'Publish courses, lessons, and assignments'),
(9, 'LMS_SUBMIT', 'LMS', 'Submit student homework and assignments'),
(10, 'EXAM_MANAGE', 'EXAMINATIONS', 'Configure examinations and scheduling'),
(11, 'MARKS_ENTRY', 'EXAMINATIONS', 'Enter and submit subject marks'),
(12, 'RESULTS_PUBLISH', 'EXAMINATIONS', 'Review, approve, and publish official report cards'),
(13, 'FEES_MANAGE', 'FEES', 'Configure fee structures, generate invoices and concession rules'),
(14, 'PAYMENTS_RECORD', 'FEES', 'Record fee payments, issue receipts, and reconcile accounts'),
(15, 'REPORTS_VIEW', 'REPORTS', 'Generate and view academic, attendance, and financial reports'),
(16, 'AUDIT_VIEW', 'AUDIT', 'Inspect system security audit logs');

-- 3. Role Permissions Mapping
-- Super Admin: all permissions (1-16)
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 10), (1, 11), (1, 12), (1, 13), (1, 14), (1, 15), (1, 16);

-- School Admin: administrative, admissions, students, fees (2, 3, 4, 5, 6, 7, 10, 12, 13, 14, 15, 16)
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
(2, 2), (2, 3), (2, 4), (2, 5), (2, 6), (2, 7), (2, 10), (2, 12), (2, 13), (2, 14), (2, 15), (2, 16);

-- Principal: academic oversight (3, 5, 7, 10, 11, 12, 15, 16)
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
(3, 3), (3, 5), (3, 7), (3, 10), (3, 11), (3, 12), (3, 15), (3, 16);

-- Teacher: attendance, LMS, marks entry (6, 7, 8, 11)
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
(4, 6), (4, 7), (4, 8), (4, 11);

-- Student: LMS submission (9)
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
(6, 9);

-- Accountant: fees and payments (13, 14, 15)
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
(7, 13), (7, 14), (7, 15);

-- 4. School Profile
INSERT IGNORE INTO school_profile (id, name, address, email, phone, logo_url, currency, timezone) VALUES
(1, 'Bluebells Schooling System', 'Adjacent Autobahn Road, Next to VIP Garden, Unit 2, Latifabad, Hyderabad, Sindh, Pakistan, 71800', 'Bluebells.schoolingsystem@gmail.com', '+92 316 9681932', '/images/bluebells-logo.png', 'PKR', 'Asia/Karachi');

-- 5. Academic Sessions & Terms
INSERT IGNORE INTO academic_sessions (id, name, start_date, end_date, is_current) VALUES
(1, '2026-2027', '2026-08-01', '2027-05-31', TRUE);

INSERT IGNORE INTO terms (id, session_id, name, start_date, end_date, weight_percentage) VALUES
(1, 1, 'First Term', '2026-08-01', '2026-12-20', 50.00),
(2, 1, 'Final Term', '2027-01-05', '2027-05-31', 50.00);

-- 6. Grade Levels
INSERT IGNORE INTO grade_levels (id, name, stage, display_order) VALUES
(1, 'Playgroup', 'PRE_PRIMARY', 1),
(2, 'Nursery', 'PRE_PRIMARY', 2),
(3, 'Kindergarten / Prep', 'PRE_PRIMARY', 3),
(4, 'Class 1', 'PRIMARY', 4),
(5, 'Class 2', 'PRIMARY', 5),
(6, 'Class 3', 'PRIMARY', 6),
(7, 'Class 4', 'PRIMARY', 7),
(8, 'Class 5', 'PRIMARY', 8),
(9, 'Class 6', 'MIDDLE', 9),
(10, 'Class 7', 'MIDDLE', 10),
(11, 'Class 8', 'MIDDLE', 11),
(12, 'Class 9 (Matric)', 'SECONDARY', 12),
(13, 'Class 10 (Matric)', 'SECONDARY', 13);

-- 7. Classes
INSERT IGNORE INTO classes (id, grade_level_id, name, code) VALUES
(1, 12, 'Class 9', 'CLS-9'),
(2, 13, 'Class 10', 'CLS-10'),
(3, 8, 'Class 5', 'CLS-5');

-- 8. Sections
INSERT IGNORE INTO sections (id, class_id, name, room_number, max_capacity) VALUES
(1, 1, 'Section A', 'Room 201', 35),
(2, 1, 'Section B', 'Room 202', 35),
(3, 2, 'Section A', 'Room 301', 35),
(4, 3, 'Rose', 'Room 101', 30);

-- 9. Subjects for Class 9
INSERT IGNORE INTO subjects (id, class_id, name, code, is_elective, total_marks) VALUES
(1, 1, 'Mathematics', 'MATH-9', FALSE, 100),
(2, 1, 'English Compulsory', 'ENG-9', FALSE, 100),
(3, 1, 'Urdu Normal', 'URD-9', FALSE, 100),
(4, 1, 'Sindhi Salees', 'SND-9', FALSE, 100),
(5, 1, 'Islamiat Compulsory', 'ISL-9', FALSE, 50),
(6, 1, 'Pakistan Studies', 'PST-9', FALSE, 50),
(7, 1, 'Physics', 'PHY-9', FALSE, 100),
(8, 1, 'Chemistry', 'CHM-9', FALSE, 100),
(9, 1, 'Computer Science', 'CS-9', TRUE, 100),
(10, 1, 'Biology', 'BIO-9', TRUE, 100);

-- 10. Grading Scales
INSERT IGNORE INTO grading_scales (id, grade, min_percentage, max_percentage, grade_point, remarks) VALUES
(1, 'A+', 80.00, 100.00, 4.00, 'Outstanding / Distinction'),
(2, 'A', 70.00, 79.99, 3.70, 'Excellent'),
(3, 'B', 60.00, 69.99, 3.00, 'Good / Very Satisfactory'),
(4, 'C', 50.00, 59.99, 2.00, 'Satisfactory'),
(5, 'D', 40.00, 49.99, 1.00, 'Pass'),
(6, 'F', 0.00, 39.99, 0.00, 'Fail');

-- 11. Fee Categories
INSERT IGNORE INTO fee_categories (id, name, is_recurring) VALUES
(1, 'Monthly Tuition Fee', TRUE),
(2, 'Admission Registration Fee', FALSE),
(3, 'Annual Examination Fee', FALSE),
(4, 'Computer & Science Lab Fee', TRUE),
(5, 'Library & Activity Charges', TRUE);
