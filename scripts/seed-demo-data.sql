-- ============================================================================
-- scripts/seed-demo-data.sql
-- Comprehensive Realistic Demonstration Data for Bluebells Schooling System
-- Latifabad Unit 2, Hyderabad, Sindh, Pakistan
-- ============================================================================

-- 1. Demo User Accounts (All default passwords: Admin@12345)
-- BCrypt Hash: $2a$12$48LaWIm/EuAioWEczLBisOlDflYK6P8G0qu7YCR2ta5xdE0Sl41wO
INSERT INTO users (id, username, email, password_hash, full_name, phone_number, status) VALUES
(1, 'admin', 'admin@bluebells.edu.pk', '$2a$12$48LaWIm/EuAioWEczLBisOlDflYK6P8G0qu7YCR2ta5xdE0Sl41wO', 'Bluebells Platform Administrator', '+92 316 9681932', 'ACTIVE'),
(2, 'principal', 'principal@bluebells.edu.pk', '$2a$12$48LaWIm/EuAioWEczLBisOlDflYK6P8G0qu7YCR2ta5xdE0Sl41wO', 'Huma Arif', '+92 300 9876543', 'ACTIVE'),
(3, 'teacher.ahmed', 'ahmed.khan@bluebells.edu.pk', '$2a$12$48LaWIm/EuAioWEczLBisOlDflYK6P8G0qu7YCR2ta5xdE0Sl41wO', 'Sir Ahmed Khan', '+92 301 2345678', 'ACTIVE'),
(4, 'teacher.fatima', 'fatima.shah@bluebells.edu.pk', '$2a$12$48LaWIm/EuAioWEczLBisOlDflYK6P8G0qu7YCR2ta5xdE0Sl41wO', 'Miss Fatima Shah', '+92 302 3456789', 'ACTIVE'),
(5, 'accountant', 'tariq.accounts@bluebells.edu.pk', '$2a$12$48LaWIm/EuAioWEczLBisOlDflYK6P8G0qu7YCR2ta5xdE0Sl41wO', 'Syed Tariq Mahmood', '+92 303 4567890', 'ACTIVE'),
(6, 'parent.khan', 'parent.khan@gmail.com', '$2a$12$48LaWIm/EuAioWEczLBisOlDflYK6P8G0qu7YCR2ta5xdE0Sl41wO', 'Mohammad Tariq Khan', '+92 300 1234567', 'ACTIVE'),
(7, 'student.ali', 'ali.khan@bluebells.edu.pk', '$2a$12$48LaWIm/EuAioWEczLBisOlDflYK6P8G0qu7YCR2ta5xdE0Sl41wO', 'Ali Khan', '+92 312 3456789', 'ACTIVE'),
(8, 'student.sara', 'sara.khan@bluebells.edu.pk', '$2a$12$48LaWIm/EuAioWEczLBisOlDflYK6P8G0qu7YCR2ta5xdE0Sl41wO', 'Sara Khan', '+92 313 4567890', 'ACTIVE')
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name), password_hash=VALUES(password_hash);

-- 2. User Roles Assignment
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES
(1, 1), -- admin -> SUPER_ADMIN
(1, 2), -- admin -> SCHOOL_ADMIN
(2, 3), -- principal -> PRINCIPAL
(3, 4), -- teacher.ahmed -> TEACHER
(4, 4), -- teacher.fatima -> TEACHER
(5, 7), -- accountant -> ACCOUNTANT
(6, 5), -- parent.khan -> PARENT
(7, 6), -- student.ali -> STUDENT
(8, 6); -- student.sara -> STUDENT

-- 3. Staff Records
INSERT INTO staff (id, user_id, employee_id, designation, department, qualification, joining_date, salary) VALUES
(1, 2, 'EMP-001', 'Principal', 'Administration', 'M.Phil Education, Sindh University', '2020-01-15', 120000.00),
(2, 3, 'EMP-014', 'Senior Faculty', 'Mathematics', 'M.Sc Mathematics, University of Sindh', '2021-08-01', 75000.00),
(3, 4, 'EMP-022', 'Senior Faculty', 'Physics & Science', 'M.Sc Physics, Mehran UET', '2022-02-01', 70000.00)
ON DUPLICATE KEY UPDATE designation=VALUES(designation);

-- 4. Teacher Assignments
INSERT IGNORE INTO teacher_assignments (id, teacher_id, section_id, subject_id, session_id, is_class_teacher) VALUES
(1, 3, 1, 1, 1, TRUE),   -- Sir Ahmed -> Class 9-A, Mathematics (Class Teacher)
(2, 4, 1, 7, 1, FALSE),  -- Miss Fatima -> Class 9-A, Physics
(3, 3, 3, 1, 1, FALSE);  -- Sir Ahmed -> Class 10-A, Mathematics

-- 5. Student Profiles
INSERT INTO students (id, user_id, admission_number, first_name, last_name, date_of_birth, gender, blood_group, b_form_number, residential_address, admission_date, status) VALUES
(1, 7, 'BSS-2026-0001', 'Ali', 'Khan', '2011-04-12', 'MALE', 'B+', '41303-1234567-3', 'House # 42, Block C, Unit 2, Latifabad, Hyderabad', '2026-08-01', 'ACTIVE'),
(2, 8, 'BSS-2026-0002', 'Sara', 'Khan', '2012-08-25', 'FEMALE', 'O+', '41303-1234567-4', 'House # 42, Block C, Unit 2, Latifabad, Hyderabad', '2026-08-01', 'ACTIVE'),
(3, NULL, 'BSS-2026-0003', 'Shahid', 'Afridi', '2011-02-18', 'MALE', 'A+', '41303-7654321-1', 'Street 4, Autobahn Road, Latifabad, Hyderabad', '2026-08-01', 'ACTIVE'),
(4, NULL, 'BSS-2026-0004', 'Ayesha', 'Siddiqui', '2011-09-10', 'FEMALE', 'B+', '41303-9988776-2', 'VIP Garden Enclave, Unit 2, Latifabad, Hyderabad', '2026-08-01', 'ACTIVE'),
(5, NULL, 'BSS-2026-0005', 'Bilal', 'Hassan', '2011-11-05', 'MALE', 'AB+', '41303-5544332-1', 'Citizen Colony, Qasimabad, Hyderabad', '2026-08-01', 'ACTIVE')
ON DUPLICATE KEY UPDATE first_name=VALUES(first_name), last_name=VALUES(last_name);

-- 6. Guardians
INSERT INTO guardians (id, user_id, full_name, relationship_type, cnic_number, phone_primary, phone_secondary, occupation, address) VALUES
(1, 6, 'Mohammad Tariq Khan', 'FATHER', '41303-1234567-1', '+92 300 1234567', '+92 316 9681932', 'Business Owner', 'House # 42, Block C, Unit 2, Latifabad, Hyderabad'),
(2, NULL, 'Abdul Rehman Afridi', 'FATHER', '41303-7654321-3', '+92 333 7654321', NULL, 'Civil Engineer', 'Street 4, Autobahn Road, Latifabad, Hyderabad')
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

-- 7. Guardian Student Links
INSERT IGNORE INTO guardian_student_links (guardian_id, student_id, is_primary_contact, is_financial_sponsor) VALUES
(1, 1, TRUE, TRUE), -- Tariq Khan -> Ali Khan
(1, 2, TRUE, TRUE), -- Tariq Khan -> Sara Khan
(2, 3, TRUE, TRUE); -- Abdul Rehman -> Shahid Afridi

-- 8. Enrollments (Academic Session 2026-2027)
INSERT INTO enrollments (id, student_id, session_id, section_id, roll_number, enrollment_status, enrollment_date) VALUES
(1, 1, 1, 1, '1', 'ACTIVE', '2026-08-01'), -- Ali Khan -> Class 9-A, Roll 1
(2, 3, 1, 1, '2', 'ACTIVE', '2026-08-01'), -- Shahid Afridi -> Class 9-A, Roll 2
(3, 4, 1, 1, '3', 'ACTIVE', '2026-08-01'), -- Ayesha Siddiqui -> Class 9-A, Roll 3
(4, 5, 1, 2, '1', 'ACTIVE', '2026-08-01'), -- Bilal Hassan -> Class 9-B, Roll 1
(5, 2, 1, 4, '1', 'ACTIVE', '2026-08-01')  -- Sara Khan -> Class 8-A (Rose), Roll 1
ON DUPLICATE KEY UPDATE roll_number=VALUES(roll_number);

-- 9. Timetable Slots (Class 9-A)
INSERT IGNORE INTO timetables (id, session_id, section_id, subject_id, teacher_id, day_of_week, start_time, end_time, room_number) VALUES
(1, 1, 1, 1, 3, 'MONDAY', '08:30:00', '09:15:00', 'Room 201'),
(2, 1, 1, 7, 4, 'MONDAY', '09:15:00', '10:00:00', 'Room 201'),
(3, 1, 1, 1, 3, 'TUESDAY', '08:30:00', '09:15:00', 'Room 201'),
(4, 1, 1, 7, 4, 'TUESDAY', '09:15:00', '10:00:00', 'Room 201'),
(5, 1, 1, 1, 3, 'WEDNESDAY', '08:30:00', '09:15:00', 'Room 201'),
(6, 1, 1, 1, 3, 'THURSDAY', '08:30:00', '09:15:00', 'Room 201'),
(7, 1, 1, 1, 3, 'FRIDAY', '08:30:00', '09:15:00', 'Room 201');

-- 10. Student Attendance Records
INSERT IGNORE INTO student_attendance (id, enrollment_id, attendance_date, status, remarks, recorded_by) VALUES
(1, 1, '2026-10-24', 'PRESENT', 'On time', 3),
(2, 2, '2026-10-24', 'PRESENT', 'On time', 3),
(3, 3, '2026-10-24', 'PRESENT', 'On time', 3),
(4, 1, '2026-10-25', 'PRESENT', 'On time', 3),
(5, 2, '2026-10-25', 'LATE', 'Traffic delay on Autobahn Road', 3),
(6, 3, '2026-10-25', 'PRESENT', 'On time', 3),
(7, 1, '2026-10-26', 'PRESENT', 'On time', 3),
(8, 2, '2026-10-26', 'PRESENT', 'On time', 3),
(9, 3, '2026-10-26', 'EXCUSED', 'Medical appointment', 3),
(10, 1, '2026-10-27', 'PRESENT', 'On time', 3),
(11, 2, '2026-10-27', 'PRESENT', 'On time', 3),
(12, 3, '2026-10-27', 'PRESENT', 'On time', 3),
(13, 1, '2026-10-28', 'PRESENT', 'On time', 3),
(14, 2, '2026-10-28', 'PRESENT', 'On time', 3),
(15, 3, '2026-10-28', 'PRESENT', 'On time', 3);

-- 11. LMS Courses & Lessons
INSERT INTO courses (id, subject_id, section_id, teacher_id, title, description, status) VALUES
(1, 1, 1, 3, 'Matriculation Mathematics Class 9', 'Comprehensive Mathematics syllabus aligned with Sindh Textbook Board curriculum.', 'PUBLISHED'),
(2, 7, 1, 4, 'Secondary Physics Class 9', 'Theoretical fundamentals, mechanics, thermodynamics, and laboratory experiments.', 'PUBLISHED')
ON DUPLICATE KEY UPDATE title=VALUES(title);

INSERT IGNORE INTO lessons (id, course_id, title, content, sequence_order, published_at) VALUES
(1, 1, 'Unit 1: Real and Complex Numbers', 'Comprehensive introduction to properties of real numbers, radicals, and complex conjugate proofs.', 1, '2026-08-15 09:00:00'),
(2, 1, 'Unit 2: Logarithms & Laws of Indices', 'Understanding scientific notation, mantissa, characteristic, and laws of logarithms.', 2, '2026-09-01 09:00:00'),
(3, 2, 'Unit 1: Physical Quantities & Measurement', 'Base SI units, derived units, vernier calipers, screw gauge, and significant figures.', 1, '2026-08-15 10:00:00');

-- 12. Assignments & Submissions
INSERT INTO assignments (id, course_id, title, instructions, max_marks, due_date, allow_late_submission, status) VALUES
(1, 1, 'Exercise 1.2 Complex Number Proofs', 'Solve questions 4 through 12 from Sindh Textbook Board Mathematics Grade 9 page 24.', 50.00, '2026-11-05 23:59:00', FALSE, 'PUBLISHED'),
(2, 2, 'Lab Manual: Density Measurement of Solids', 'Submit experimental observation table and calculated density with percentage error.', 25.00, '2026-11-10 23:59:00', FALSE, 'PUBLISHED')
ON DUPLICATE KEY UPDATE title=VALUES(title);

INSERT IGNORE INTO assignment_submissions (id, assignment_id, student_id, submission_text, file_url, submitted_at, is_late, status, marks_obtained, teacher_feedback, graded_at, graded_by) VALUES
(1, 1, 1, 'Attached complete handwritten derivation of complex conjugate questions 4 to 12.', NULL, '2026-10-27 16:30:00', FALSE, 'GRADED', 48.00, 'Excellent algebraic working and neat presentation. Distinction-level work!', '2026-10-28 10:00:00', 3);

-- 13. Examinations, Marks & Report Cards
INSERT INTO exams (id, session_id, term_id, name, start_date, end_date, status) VALUES
(1, 1, 1, 'Mid-Term Examination 2026-2027', '2026-10-15', '2026-10-25', 'PUBLISHED')
ON DUPLICATE KEY UPDATE name=VALUES(name);

INSERT IGNORE INTO marks_entries (id, exam_id, subject_id, student_id, marks_obtained, max_marks, grade, is_absent, status, recorded_by) VALUES
(1, 1, 1, 1, 94.00, 100.00, 'A+', FALSE, 'PUBLISHED', 3),
(2, 1, 7, 1, 88.00, 100.00, 'A+', FALSE, 'PUBLISHED', 4),
(3, 1, 1, 3, 85.00, 100.00, 'A+', FALSE, 'PUBLISHED', 3),
(4, 1, 7, 3, 82.00, 100.00, 'A+', FALSE, 'PUBLISHED', 4);

INSERT INTO report_cards (id, exam_id, student_id, enrollment_id, total_marks_obtained, total_max_marks, overall_percentage, overall_grade, attendance_percentage, class_rank, remarks, status) VALUES
(1, 1, 1, 1, 182.00, 200.00, 91.00, 'A+', 96.00, 1, 'Outstanding academic performance. Exemplary discipline and dedication.', 'PUBLISHED'),
(2, 1, 3, 2, 167.00, 200.00, 83.50, 'A+', 92.00, 2, 'Very good analytical capability and consistent participation.', 'PUBLISHED')
ON DUPLICATE KEY UPDATE overall_percentage=VALUES(overall_percentage);

-- 14. Fee Structures & Monthly Invoices
INSERT IGNORE INTO fee_structures (id, session_id, class_id, category_id, amount, frequency) VALUES
(1, 1, 1, 1, 8500.00, 'MONTHLY'), -- Class 9 Tuition: 8,500 PKR
(2, 1, 1, 4, 1500.00, 'MONTHLY'), -- Class 9 Lab: 1,500 PKR
(3, 1, 3, 1, 7500.00, 'MONTHLY'); -- Class 8 Tuition: 7,500 PKR

INSERT INTO fee_invoices (id, invoice_number, student_id, session_id, billing_month, total_amount, discount_amount, paid_amount, balance_due, due_date, status) VALUES
(1, 'INV-202610-0001', 1, 1, '2026-10', 10000.00, 0.00, 10000.00, 0.00, '2026-10-10', 'PAID'),
(2, 'INV-202610-0002', 3, 1, '2026-10', 10000.00, 0.00, 5000.00, 5000.00, '2026-10-10', 'PARTIAL'),
(3, 'INV-202610-0003', 5, 1, '2026-10', 10000.00, 0.00, 0.00, 10000.00, '2026-10-10', 'UNPAID')
ON DUPLICATE KEY UPDATE status=VALUES(status);

INSERT IGNORE INTO fee_invoice_items (id, invoice_id, category_id, amount) VALUES
(1, 1, 1, 8500.00),
(2, 1, 4, 1500.00),
(3, 2, 1, 8500.00),
(4, 2, 4, 1500.00),
(5, 3, 1, 8500.00),
(6, 3, 4, 1500.00);

-- 15. Payments & Allocations
INSERT INTO payments (id, payment_reference, student_id, amount, payment_date, payment_method, received_by, notes, status) VALUES
(1, 'REC-202610-0001', 1, 10000.00, '2026-10-05', 'ONLINE_BANK_TRANSFER', 5, 'Meezan Bank Online Transfer ref # PK98234710', 'CLEARED'),
(2, 'REC-202610-0002', 3, 5000.00, '2026-10-08', 'CASH', 5, 'Over-the-counter cash collection at school accounts office', 'CLEARED')
ON DUPLICATE KEY UPDATE amount=VALUES(amount);

INSERT IGNORE INTO payment_allocations (id, payment_id, invoice_id, allocated_amount) VALUES
(1, 1, 1, 10000.00),
(2, 2, 2, 5000.00);

-- 16. Admission Applications
INSERT INTO admission_applications (id, application_number, applicant_name, date_of_birth, gender, applied_class_id, guardian_name, guardian_phone, guardian_cnic, status, notes) VALUES
(1, 'APP-2026-0001', 'Shahid Afridi', '2011-02-18', 'MALE', 1, 'Abdul Rehman Afridi', '+92 333 7654321', '41303-7654321-3', 'ENROLLED', 'Enrolled as student BSS-2026-0003'),
(2, 'APP-2026-0002', 'Hamza Tariq', '2013-05-14', 'MALE', 3, 'Tariq Mahmood', '+92 321 9876543', '41303-1122334-1', 'APPROVED', 'Passed admission assessment with 88% marks'),
(3, 'APP-2026-0003', 'Zainab Fatima', '2012-07-20', 'FEMALE', 1, 'Dr. Mansoor', '+92 345 5566778', '41303-9900112-2', 'SUBMITTED', 'Awaiting test date scheduling')
ON DUPLICATE KEY UPDATE status=VALUES(status);

-- 17. System Notifications
INSERT IGNORE INTO notifications (id, recipient_id, title, message, category, is_read) VALUES
(1, 1, 'Admission Application Received', 'New application APP-2026-0003 received for Class 9 (Zainab Fatima).', 'ADMISSION', FALSE),
(2, 6, 'Tuition Fee Acknowledgment', 'Payment of Rs. 10,000 for Ali Khan (INV-202610-0001) has been received and cleared.', 'FINANCE', FALSE),
(3, 6, 'Mid-Term Report Card Published', 'Official Report Card for Ali Khan is now available for download. Overall Grade: A+.', 'EXAMINATION', FALSE),
(4, 7, 'Assignment Published: Exercise 1.2', 'Sir Ahmed published a new assignment for Mathematics. Due on Nov 5, 2026.', 'LMS', FALSE);
