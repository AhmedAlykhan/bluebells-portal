-- ============================================================================
-- V3__lms_enhancements.sql
-- Comprehensive LMS Enhancements: Modules, Multi-Media Resources, YouTube Integration,
-- Multi-Photo Assignment Submissions, and Student Learning Progress
-- ============================================================================

-- 1. Course Modules (Curricular Units / Chapters)
CREATE TABLE IF NOT EXISTS lms_course_modules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    display_order INT NOT NULL DEFAULT 1,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_modules_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Enhance Lessons Table
ALTER TABLE lessons 
    ADD COLUMN module_id BIGINT NULL AFTER course_id,
    ADD COLUMN lesson_type VARCHAR(32) NOT NULL DEFAULT 'LECTURE' AFTER content,
    ADD COLUMN duration_minutes INT NOT NULL DEFAULT 0 AFTER lesson_type,
    ADD CONSTRAINT fk_lessons_module FOREIGN KEY (module_id) REFERENCES lms_course_modules(id) ON DELETE SET NULL;

-- 3. Learning Resources (PDF Notes, Past Papers, Syllabi, Slide Decks, Lab Manuals)
CREATE TABLE IF NOT EXISTS lms_learning_resources (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    module_id BIGINT NULL,
    lesson_id BIGINT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    resource_type VARCHAR(32) NOT NULL DEFAULT 'PDF',
    file_path VARCHAR(512) NULL,
    original_filename VARCHAR(255) NULL,
    file_size_bytes BIGINT NULL,
    content_type VARCHAR(128) NULL,
    external_url VARCHAR(1024) NULL,
    is_downloadable BOOLEAN NOT NULL DEFAULT TRUE,
    uploaded_by BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_resources_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    CONSTRAINT fk_resources_module FOREIGN KEY (module_id) REFERENCES lms_course_modules(id) ON DELETE SET NULL,
    CONSTRAINT fk_resources_lesson FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE SET NULL,
    CONSTRAINT fk_resources_uploader FOREIGN KEY (uploaded_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. YouTube Video Lectures
CREATE TABLE IF NOT EXISTS lms_youtube_videos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    module_id BIGINT NULL,
    lesson_id BIGINT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    youtube_url VARCHAR(1024) NOT NULL,
    video_id VARCHAR(64) NOT NULL,
    duration_seconds INT NOT NULL DEFAULT 0,
    display_order INT NOT NULL DEFAULT 1,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_yt_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    CONSTRAINT fk_yt_module FOREIGN KEY (module_id) REFERENCES lms_course_modules(id) ON DELETE SET NULL,
    CONSTRAINT fk_yt_lesson FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Enhance Assignments Table
ALTER TABLE assignments
    ADD COLUMN module_id BIGINT NULL AFTER course_id,
    ADD COLUMN passing_marks DECIMAL(5,2) NOT NULL DEFAULT 40.00 AFTER max_marks,
    ADD COLUMN allowed_formats VARCHAR(128) NOT NULL DEFAULT 'PDF,IMAGES,DOCX' AFTER allow_late_submission,
    ADD COLUMN attachment_url VARCHAR(512) NULL AFTER allowed_formats,
    ADD COLUMN attachment_name VARCHAR(255) NULL AFTER attachment_url,
    ADD COLUMN attachment_size_bytes BIGINT NULL AFTER attachment_name,
    ADD CONSTRAINT fk_assignments_module FOREIGN KEY (module_id) REFERENCES lms_course_modules(id) ON DELETE SET NULL;

-- 6. Assignment Multi-Photo / Multi-File Submissions (Handwritten Homework Pages)
CREATE TABLE IF NOT EXISTS lms_submission_files (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    submission_id BIGINT NOT NULL,
    file_path VARCHAR(512) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    file_size_bytes BIGINT NOT NULL,
    file_type VARCHAR(64) NOT NULL DEFAULT 'IMAGE',
    upload_order INT NOT NULL DEFAULT 1,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_sub_files_submission FOREIGN KEY (submission_id) REFERENCES assignment_submissions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Lesson & Course Progress Tracking
CREATE TABLE IF NOT EXISTS lms_lesson_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    lesson_id BIGINT NOT NULL,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at DATETIME(6) NULL,
    last_accessed_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    UNIQUE KEY uk_student_lesson_progress (student_id, lesson_id),
    CONSTRAINT fk_progress_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_progress_lesson FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. Seed Baseline Courses, Modules, Lessons, Resources & Videos
-- Ensure Teacher User 'teacher_ahmed' exists for realistic persona
INSERT IGNORE INTO users (id, username, email, full_name, password_hash, phone_number, status) VALUES
(2, 'teacher_ahmed', 'ahmed.khan@bluebells.edu.pk', 'Sir Ahmed Khan', '$2a$10$wE9mHlW8U2yS/1z7CqF2beGj5mE8fG9hK1lM3nO5pQ7rS9tU1vW3x', '+92 300 1234567', 'ACTIVE'),
(3, 'teacher_fatima', 'fatima.shah@bluebells.edu.pk', 'Miss Fatima Shah', '$2a$10$wE9mHlW8U2yS/1z7CqF2beGj5mE8fG9hK1lM3nO5pQ7rS9tU1vW3x', '+92 300 7654321', 'ACTIVE');

-- Assign ROLE_TEACHER to teachers
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES
(2, 4),
(3, 4);

-- Seed Baseline Courses for Class 9 Section A
INSERT IGNORE INTO courses (id, subject_id, section_id, teacher_id, title, description, status) VALUES
(1, 1, 1, 2, 'Matriculation Mathematics Class 9', 'Comprehensive course covering Real Numbers, Logarithms, Algebraic Expressions, Matrices, and Euclidean Geometry according to the Sindh Textbook Board syllabus.', 'PUBLISHED'),
(2, 7, 1, 3, 'Secondary Physics Class 9', 'Foundational Physics addressing Physical Quantities, Kinematics, Dynamics, Gravitation, Work & Energy, and Matter Properties with hands-on lab experiments.', 'PUBLISHED'),
(3, 9, 1, 2, 'Computer Science Class 9', 'Introduction to Computer Systems, Computer Architecture, Number Systems, Algorithms, and Flowcharts with practical computing workshops.', 'PUBLISHED');

-- Seed Modules for Mathematics
INSERT IGNORE INTO lms_course_modules (id, course_id, title, description, display_order) VALUES
(1, 1, 'Unit 1: Real and Complex Numbers', 'Representation of real numbers, radicals, exponents, complex number conjugates, and basic operations.', 1),
(2, 1, 'Unit 2: Logarithms & Laws of Indices', 'Scientific notation, common logarithms, characteristic, mantissa, and logarithmic computation laws.', 2),
(3, 1, 'Unit 3: Algebraic Expressions & Formulas', 'Operations on algebraic expressions, special product identities, factorisation, and HCF/LCM.', 3);

-- Seed Modules for Physics
INSERT IGNORE INTO lms_course_modules (id, course_id, title, description, display_order) VALUES
(4, 2, 'Unit 1: Physical Quantities & Measurement', 'International System of Units (SI), Vernier Callipers, Screw Gauge, Physical Balance, and Significant Figures.', 1),
(5, 2, 'Unit 2: Kinematics & Motion', 'Translatory, rotatory and vibratory motion, distance vs displacement, speed vs velocity, acceleration, and equations of motion.', 2);

-- Seed Modules for Computer Science
INSERT IGNORE INTO lms_course_modules (id, course_id, title, description, display_order) VALUES
(6, 3, 'Unit 1: Fundamentals of Computer Systems', 'Generations of computers, hardware components, Von Neumann architecture, and primary/secondary storage.', 1);

-- Seed Lessons
INSERT IGNORE INTO lessons (id, course_id, module_id, title, content, sequence_order, lesson_type, duration_minutes, published_at) VALUES
(1, 1, 1, 'Introduction to Complex Numbers & Conjugates', 'Detailed study of imaginary unit i = sqrt(-1), standard form z = a + bi, addition, subtraction, multiplication, and conjugate properties.', 1, 'LECTURE', 45, '2026-08-15 09:00:00'),
(2, 1, 1, 'Properties of Real Numbers & Algebraic Proofs', 'Closure, Commutative, Associative, Distributive, Identity, and Inverse properties over addition and multiplication.', 2, 'LECTURE', 40, '2026-08-18 09:00:00'),
(3, 1, 2, 'Scientific Notation, Characteristic & Mantissa', 'Expressing large and small numbers in scientific format, finding characteristic by inspection, and reading 4-figure log tables.', 3, 'LECTURE', 45, '2026-08-25 09:00:00'),
(4, 1, 2, 'Laws of Logarithms & Practical Calculation', 'Proof of log(ab) = log a + log b, log(a/b) = log a - log b, log(a^n) = n log a, and change of base theorem.', 4, 'LECTURE', 50, '2026-08-28 09:00:00'),
(5, 2, 4, 'Vernier Callipers & Screw Gauge Precision', 'Measuring internal/external diameter and depth with 0.1mm Vernier Callipers, pitch and zero error analysis.', 1, 'LECTURE', 50, '2026-08-16 10:30:00'),
(6, 2, 4, 'Physical Balance, Stop Watch & Significant Figures', 'Measuring mass, measuring cylinder meniscus reading, precision in laboratory experiments, and rounding rules.', 2, 'LECTURE', 40, '2026-08-20 10:30:00'),
(7, 3, 6, 'Von Neumann Architecture & CPU Execution Cycles', 'Structure of the CPU: ALU, Control Unit, Registers, System Bus, and the Fetch-Decode-Execute instruction cycle.', 1, 'LECTURE', 45, '2026-08-17 11:30:00');

-- Seed Learning Resources (Curriculum Materials & Reference Docs)
INSERT IGNORE INTO lms_learning_resources (id, course_id, module_id, lesson_id, title, description, resource_type, file_path, original_filename, file_size_bytes, content_type, external_url, is_downloadable, uploaded_by) VALUES
(1, 1, 1, 1, 'Class 9 Mathematics Unit 1 Comprehensive Study Notes', 'Complete solved examples, theorems, and exercise formula sheets for Real and Complex Numbers.', 'PDF', '/storage/uploads/math9_unit1_notes.pdf', 'math9_unit1_notes.pdf', 2450000, 'application/pdf', NULL, TRUE, 2),
(2, 1, 2, 3, 'Logarithm Tables & Practice Problem Worksheet', 'Four-figure common logarithm and anti-logarithm tables with 50 practice drill exercises.', 'PDF', '/storage/uploads/log_tables_practice.pdf', 'log_tables_practice.pdf', 1820000, 'application/pdf', NULL, TRUE, 2),
(3, 2, 4, 5, 'Physics Secondary Practical Manual: Measurement of Diameter & Volume', 'Official laboratory manual with zero error calibration charts, observation tables, and ray diagrams.', 'PDF', '/storage/uploads/physics_lab_manual_unit1.pdf', 'physics_lab_manual_unit1.pdf', 3150000, 'application/pdf', NULL, TRUE, 3),
(4, 3, 6, 7, 'Von Neumann Architecture Slide Deck (Computer Science)', 'Instructional slides illustrating the CPU bus architecture, registers, and memory hierarchy.', 'PPTX', '/storage/uploads/von_neumann_slides.pptx', 'von_neumann_slides.pptx', 4200000, 'application/vnd.openxmlformats-officedocument.presentationml.presentation', NULL, TRUE, 2);

-- Seed YouTube Educational Video Lectures
INSERT IGNORE INTO lms_youtube_videos (id, course_id, module_id, lesson_id, title, description, youtube_url, video_id, duration_seconds, display_order) VALUES
(1, 1, 1, 1, 'Understanding Complex Numbers: Real & Imaginary Parts', 'Clear conceptual breakdown of the complex plane, imaginary unit i, and algebraic conjugates.', 'https://www.youtube.com/watch?v=SP-YJe7Vldo', 'SP-YJe7Vldo', 840, 1),
(2, 1, 2, 4, 'Laws of Logarithms with Practical Step-by-Step Proofs', 'Visual proofs of logarithmic laws with worked examples from past Matriculation board exams.', 'https://www.youtube.com/watch?v=Z5myJ8dg_rM', 'Z5myJ8dg_rM', 960, 2),
(3, 2, 4, 5, 'How to Read a Vernier Caliper (Zero Error & Vernier Scale)', 'Detailed demonstration on using a metric Vernier caliper to measure cylindrical diameter.', 'https://www.youtube.com/watch?v=vkPlzmalvN4', 'vkPlzmalvN4', 720, 1),
(4, 3, 6, 7, 'The Von Neumann Architecture Explained', 'Animated guide to CPU architecture, busses, register files, and program counter operations.', 'https://www.youtube.com/watch?v=KBmoqwVt4Qg', 'KBmoqwVt4Qg', 650, 1);

-- Seed Assignments
INSERT IGNORE INTO assignments (id, course_id, module_id, title, instructions, max_marks, passing_marks, due_date, allow_late_submission, allowed_formats, status, attachment_url, attachment_name, attachment_size_bytes) VALUES
(1, 1, 1, 'Exercise 1.2 Mathematical Proofs & Complex Conjugates', 'Solve Questions 1 through 8 from Exercise 1.2 in your mathematics homework notebook. Write clean steps showing proof of conjugates and modulus calculations. Take clear photos of your notebook pages and upload them.', 50.00, 20.00, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 DAY), TRUE, 'IMAGES,PDF', 'PUBLISHED', '/storage/uploads/math9_hw_ex1_2.pdf', 'math9_hw_ex1_2.pdf', 450000),
(2, 1, 2, 'Logarithm Laws Application & Numerical Problems', 'Calculate expressions using 4-figure logarithmic tables. Show full characteristic and mantissa steps on paper. Submit PDF or photos of your handwritten working.', 40.00, 16.00, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 10 DAY), FALSE, 'IMAGES,PDF,DOCX', 'PUBLISHED', NULL, NULL, NULL),
(3, 2, 4, 'Lab Assignment: Density & Volume Measurement Using Vernier Calipers', 'Complete the observation table from page 14 of the Physics Lab Manual. Record zero error, main scale reading, and Vernier coincidence for the given metal cylinder. Calculate density with error bounds.', 25.00, 10.00, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 7 DAY), TRUE, 'IMAGES,PDF', 'PUBLISHED', '/storage/uploads/physics_density_lab_worksheet.pdf', 'physics_density_lab_worksheet.pdf', 380000);

-- Seed Sample Submission for Student (Ali Khan, student_id = 1 if enrolled, or safe link)
-- Ensure Ali Khan is linked to Class 9 Section A
INSERT IGNORE INTO assignment_submissions (id, assignment_id, student_id, submission_text, file_url, submitted_at, is_late, status, marks_obtained, teacher_feedback, graded_at, graded_by) VALUES
(1, 1, 1, 'Dear Sir Ahmed, I have completed all 8 questions of Exercise 1.2 in my notebook with complete proofs and diagrams. Handwritten pages attached.', '/storage/uploads/submissions/ali_ex1_2_pages.pdf', CURRENT_TIMESTAMP, FALSE, 'GRADED', 48.00, 'Distinction-level work Ali! Excellent conceptual clarity on conjugate verification. Keep up the high standard.', CURRENT_TIMESTAMP, 2);

-- Seed Multi-Photo Submission Files for Ali Khan's Homework
INSERT IGNORE INTO lms_submission_files (id, submission_id, file_path, original_filename, file_size_bytes, file_type, upload_order) VALUES
(1, 1, '/storage/uploads/submissions/ali_homework_page1.jpg', 'Exercise_1_2_Page_1.jpg', 1250000, 'IMAGE', 1),
(2, 1, '/storage/uploads/submissions/ali_homework_page2.jpg', 'Exercise_1_2_Page_2.jpg', 1320000, 'IMAGE', 2),
(3, 1, '/storage/uploads/submissions/ali_homework_page3.jpg', 'Exercise_1_2_Page_3.jpg', 1180000, 'IMAGE', 3);

-- Seed Lesson Progress for Ali Khan
INSERT IGNORE INTO lms_lesson_progress (student_id, lesson_id, is_completed, completed_at, last_accessed_at) VALUES
(1, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 2, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 5, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
