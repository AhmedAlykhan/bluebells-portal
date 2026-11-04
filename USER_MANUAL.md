# Bluebells School LMS & School Management Portal — User Manual

## Institution Profile
- **Institution**: Bluebells Schooling System
- **Campus**: Adjacent Autobahn Road, Next to VIP Garden, Unit 2, Latifabad, Hyderabad, Sindh, Pakistan (Postal Code: 71800)
- **Contact**: `Bluebells.schoolingsystem@gmail.com` | `+92 316 9681932`
- **Portal URL**: `http://localhost:5173`
- **Backend API**: `http://localhost:8080/api/v1`

---

## 1. System Roles & Access Hierarchy

The portal enforces strict Role-Based Access Control (RBAC):
- **Super Administrator (`ROLE_SUPER_ADMIN`)**: Unrestricted administrative access across all operational, financial, and security domains.
- **School Administrator (`ROLE_SCHOOL_ADMIN`)**: Admissions processing, student enrollment, fee structures, invoicing, notifications, and reports.
- **Principal (`ROLE_PRINCIPAL`)**: Academic calendar, staff subject assignments, exam publishing, and executive analytics.
- **Teacher (`ROLE_TEACHER`)**: Timetable viewing, daily section attendance marking, course material authoring, assignment grading, and marks entry.
- **Parent / Guardian (`ROLE_PARENT`)**: Child profile viewing, attendance history, homework submissions, exam report cards, and fee invoices (strictly isolated to linked children).
- **Student (`ROLE_STUDENT`)**: Personal timetable, enrolled courses, homework submission, exam results, and fee status.

---

## 2. Administrator & Principal Operations

### Academic Setup
1. **Academic Session & Terms**:
   - Access **Academics** > **Sessions**.
   - Create academic session (e.g. `2026-2027`) and mark as active.
   - Configure Terms (e.g. `First Term`, `Mid Term`, `Final Term`).
2. **Grade Levels, Classes, and Sections**:
   - Define grade levels (e.g. `Secondary / Matriculation`).
   - Create Classes (e.g. `Grade 9`, `Grade 10`).
   - Create Sections (e.g. `Section A`, `Section B`) with maximum capacity limits.
3. **Subjects & Staff Assignments**:
   - Define core and elective subjects according to the Sindh Board curriculum.
   - Assign teaching staff to class subjects with designated weekly periods.

### Admissions & Student Enrollment
1. **Online Applications**:
   - Prospective parents submit applications via the public admission form.
   - Applications receive a reference: `APP-YYYY-XXXX`.
2. **Review & Conversion**:
   - Administrator reviews applicant details in **Admissions** > **Applications**.
   - Upon acceptance, click **Enroll Student**. The system automatically:
     - Assigns official Student ID (`BSS-YYYY-XXXX`).
     - Creates student profile and active enrollment in target class and section.
     - Creates or links the parent/guardian profile with primary contact flag.

### Fee Structure Definition & Invoicing
1. **Fee Structures**:
   - Navigate to **Finance** > **Fee Structures**.
   - Configure class-specific amounts for Monthly Tuition, Admission, and Annual charges in PKR.
2. **Batch Monthly Invoicing**:
   - At the beginning of each billing month (e.g. `2026-11`), execute **Generate Monthly Invoices**.
   - Invoices are automatically generated with itemized fee components for all enrolled students.
3. **Recording Payments**:
   - When parents pay via Cash, Bank Transfer, EasyPaisa, or JazzCash:
   - Navigate to **Finance** > **Collect Fee**.
   - Enter Student ID and amount.
   - The system automatically allocates payments using FIFO logic across unpaid invoices and prints/downloads an official payment receipt.

---

## 3. Teacher Operations

### Timetable & Daily Attendance
1. **Schedule Inspection**:
   - View weekly section timetable under **Academics** > **Timetable**.
   - All periods are protected by automated 3-way clash detection.
2. **Marking Attendance**:
   - Open **Attendance** > **Mark Daily Attendance**.
   - Select Date and Section.
   - The system displays the student roster with default `PRESENT` status.
   - Toggle students to `ABSENT`, `LATE`, or `EXCUSED` as needed and click **Submit**.
   - Duplicate submissions for the same section and date are automatically guarded against.
3. **Attendance Corrections**:
   - If an error occurred, submit a correction request with mandatory justification. All modifications are permanently recorded in the audit log.

### Coursework & LMS
1. **Course & Lesson Management**:
   - Navigate to **LMS** > **My Courses**.
   - Add lessons with sequence ordering, rich markdown content, and learning objectives.
2. **Distributing Assignments**:
   - Click **Create Assignment** within any course.
   - Set maximum marks and due date/time.
3. **Grading Submissions**:
   - Open submitted student coursework.
   - Enter marks obtained (system rejects marks exceeding maximum marks) and qualitative feedback.

### Examinations & Marks Entry
1. **Entering Marks**:
   - Navigate to **Examinations** > **Marks Entry**.
   - Select Exam and Subject.
   - Enter marks for each student. Mark absent students accordingly.
2. **Result Verification**:
   - Principal reviews entered marks and executes **Publish Results**.
   - The system computes totals, overall percentages, assigns Sindh Board grades (A+, A, B, C, D, F), calculates class rankings, and locks marks entries against future changes.

---

## 4. Parent / Guardian Operations

### Multi-Child Dashboard
- Parents linked to multiple children can seamlessly switch between their children from the top navigation selector.
- Complete data privacy is enforced: parents have zero visibility into any unlinked students.

### Checking Attendance & Academics
- View real-time daily attendance calendars and monthly attendance percentages.
- View courses, lesson materials, and upcoming homework assignments for their child.

### Viewing Report Cards
- Once exam results are published, navigate to **Academics** > **Report Cards**.
- View subject-wise breakdown, total marks obtained, percentage, overall grade, and class rank.

### Fee Invoices & Payment Ledger
- Open **Finance** > **Fee Ledger**.
- Inspect current month invoices, due dates, past payment receipts, and outstanding balances in PKR.

---

## 5. Security & Data Protection Guidelines
- **Password Strength**: Minimum 8 characters with at least one uppercase letter, one digit, and one special character.
- **Session Expiry**: Inactive JWT tokens expire automatically after 24 hours.
- **CSV Data Safety**: All downloaded spreadsheets are sanitized against CSV formula injection.
- **Reporting Inconsistencies**: Contact school IT support immediately at `Bluebells.schoolingsystem@gmail.com`.
