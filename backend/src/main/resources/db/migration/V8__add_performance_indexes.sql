-- =========================================================================
-- V8: Add performance indexes on hot foreign keys and high-frequency queries
-- =========================================================================

-- 1. Student Enrollments
CREATE INDEX idx_enrollments_student_status ON enrollments(student_id, enrollment_status);
CREATE INDEX idx_enrollments_section_status ON enrollments(section_id, enrollment_status);

-- 2. Student Attendance
CREATE INDEX idx_attendance_enrollment_date ON student_attendance(enrollment_id, attendance_date);
CREATE INDEX idx_attendance_date_status ON student_attendance(attendance_date, status);

-- 3. Fee Invoices & Items
CREATE INDEX idx_fee_invoices_due_date_status ON fee_invoices(due_date, status);
CREATE INDEX idx_fee_invoices_session_month ON fee_invoices(session_id, billing_month);
CREATE INDEX idx_fee_invoice_items_invoice ON fee_invoice_items(invoice_id);
CREATE INDEX idx_payments_student_date ON payments(student_id, payment_date);

-- 4. Examination & Marks
CREATE INDEX idx_marks_entries_student ON marks_entries(student_id);
CREATE INDEX idx_marks_entries_exam_subject ON marks_entries(exam_id, subject_id);
CREATE INDEX idx_report_cards_student_exam ON report_cards(student_id, exam_id);

-- 5. Staff & LMS
CREATE INDEX idx_teacher_assignments_teacher_sec ON teacher_assignments(teacher_id, section_id);
CREATE INDEX idx_assignment_submissions_assign_stud ON assignment_submissions(assignment_id, student_id);
CREATE INDEX idx_courses_section_subject ON courses(section_id, subject_id);
