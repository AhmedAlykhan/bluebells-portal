# Bluebells Schooling System — Interactive Feature & Workflow Audit Report

**Institution**: Bluebells Schooling System  
**Campus**: Adjacent Autobahn Road, Next to VIP Garden, Unit 2, Latifabad, Hyderabad, Sindh, Pakistan (Postal Code: 71800)  
**Academic Session**: 2026–2027  
**Date of Audit**: October 2026  
**Auditor**: Senior Full-Stack & Application Security Engineering Team  
**Status**: VERIFIED & PRODUCTION READY

---

## Executive Summary

Prior to this audit, several interactive controls across the portal shell either pointed to mock placeholders, lacked client-to-backend API wiring, or leaked administrative privileges to student and teacher accounts. 

This audit report documents the remediation and verification of six critical interactive workflows. All six workflows are now backed by real Spring Boot JPA endpoints, transactional database mutations on MySQL 8.x, role-guarded UI components, and automated regression test suites.

---

## Audit Item 1: Sindh Board Matriculation Report Card & Transcript

- **Component**: `frontend/src/components/modals/ReportCardModal.tsx`
- **Observed Defect**: Students and parents had no means to generate or preview their official Board examination transcript. The examinations tab presented a generic table without printing capabilities or Sindh Board formatting.
- **Root Cause**: Missing modal implementation and lack of a dedicated self-service report card endpoint in `ExamController`.
- **Implementation & Resolution**:
  1. Built `ReportCardModal.tsx` formatted in compliance with Board of Intermediate & Secondary Education (BISE) Hyderabad guidelines:
     - Header: Institutional insignia, Bluebells Schooling System Latifabad, Board Affiliation No., Roll Number, Examination Session (Annual 2026).
     - Subject breakdown table: Mathematics, Physics, Chemistry, Computer Science, English Compulsory, Urdu Salees, Sindhi Salees, and Islamiat/Ethics.
     - Metrics: Total Marks (850), Obtained Marks (762), Percentage (89.6%), Grade (A-1 Distinction), GPA (3.92), Division (First).
     - Signatures: Class Teacher (Sir Ahmed Khan), Headmaster/Principal (Dr. Asim Farooqui), and Examination Controller.
     - Controls: Integrated `window.print()` trigger with dedicated CSS print styling (`@media print`) allowing instant PDF generation or paper printing.
  2. Implemented backend self-service endpoint `GET /api/v1/exams/my-report-cards` and IDOR-guarded `GET /api/v1/exams/students/{studentId}/report-cards` in `ExamController.java`.
- **Verification Evidence**:
  - Accessible directly from `StudentDashboard` via "View Sindh Board Report Card" button.
  - Accessible from `ParentDashboard` for each linked child.
  - Tested in Vitest suite (`App.test.tsx`) and verified print layout.

---

## Audit Item 2: New Admission Application Workflow

- **Component**: `frontend/src/components/modals/NewAdmissionModal.tsx`
- **Observed Defect**: Clicking the "+ New Admission" button on the admissions page was non-functional or threw console warnings.
- **Root Cause**: The button was not attached to an active form modal, and form submission lacked payload formatting for `POST /api/v1/admissions/applications`.
- **Implementation & Resolution**:
  1. Created `NewAdmissionModal.tsx` with comprehensive field validation:
     - Student Full Name, Gender, Date of Birth.
     - Father / Guardian Name, National Identity CNIC (`41303-xxxxxxx-x` format validation), Contact Number (`+92 3xx xxxxxxx`).
     - Target Class / Grade (Nursery through Class 10 Matriculation Science/Arts).
     - Previous School Name, Residential Address (Latifabad, Hyderabad).
  2. Connected modal directly to `POST /api/v1/admissions/applications`:
     - Dispatches real JSON request to Spring Boot `AdmissionController`.
     - Validates applicant age and generates sequential application tracking numbers (`APP-2026-XXXX`).
     - Emits success toast, resets form state, and invokes parent callback to refresh admissions table.
- **Verification Evidence**:
  - Verified in `AdmissionToEnrollmentLifecycleTest.java` (backend JUnit 5).
  - Verified UI rendering and form submission in `AdmissionsPage` and `AdminDashboard`.

---

## Audit Item 3: Bulk Attendance Marking Modal

- **Component**: `frontend/src/components/modals/MarkAttendanceModal.tsx`
- **Observed Defect**: Teachers and administrators could view historical attendance but lacked a functional UI to record daily morning attendance.
- **Root Cause**: Attendance controller expected bulk records (`AttendanceRecordRequest`), but frontend lacked section-based roster loading and batch submission dialog.
- **Implementation & Resolution**:
  1. Created `MarkAttendanceModal.tsx`:
     - Dynamic class section selector (Class 9-A, 10-A, etc.).
     - Attendance date picker (defaulted to current date in `Asia/Karachi` timezone).
     - Live student roster loaded dynamically.
     - Interactive per-student status toggle buttons: `PRESENT` (green), `ABSENT` (red), `LATE` (yellow), `EXCUSED` (blue).
     - 1-click convenience action: "Mark All Present".
     - Optional remarks input per student.
  2. Connected to backend `POST /api/v1/attendance/mark`:
     - Protected by `@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER')")`.
     - Evaluated against `authorizationService.checkAttendanceMarking` to verify teacher is authorized for that class section.
- **Verification Evidence**:
  - Verified in `AttendanceLifecycleAndAuditTest.java` (backend JUnit 5).
  - Verified in `TeacherDashboard` ("Mark Today's Attendance") and `AttendancePage`.

---

## Audit Item 4: Scoped Timetable & Class Schedules

- **Component**: `frontend/src/pages/dashboards/StudentDashboard.tsx` & `ModulePages.tsx` (`TimetablePage`)
- **Observed Defect**: Students could view master school timetable schedules including teacher-only free periods and other grades.
- **Root Cause**: `TimetableController` lacked a `/my` endpoint; the frontend displayed static master schedule data to all users regardless of role.
- **Implementation & Resolution**:
  1. Added `GET /api/v1/timetable/my` in `TimetableController.java` and `TimetableService.java`:
     - Resolves the authenticated user's role.
     - For students: finds their enrolled section and returns only periods for their section.
     - For teachers: finds all sections and subjects where the teacher is actively assigned.
     - For parents: returns periods for their child's section.
  2. Embedded "Today's Schedule" widget in `StudentDashboard.tsx` showing current period, subject, assigned teacher, room location, and live period highlight.
  3. Scoped `TimetablePage.tsx` so students and parents only see their relevant timetable, while administrators retain full campus schedule views with clash detection.
- **Verification Evidence**:
  - Verified in `TimetableClashDetectionTest.java` (backend JUnit 5).
  - Verified in frontend role-based routing tests.

---

## Audit Item 5: Notification Center Slide-Out Drawer

- **Component**: `frontend/src/components/notifications/NotificationDrawer.tsx`
- **Observed Defect**: Header bell icon was static, with no badge count or interactive popup.
- **Root Cause**: Absence of notification drawer component and missing mark-as-read integration.
- **Implementation & Resolution**:
  1. Built `NotificationDrawer.tsx`:
     - Slide-out right-hand drawer with smooth overlay and transition animations.
     - Real-time notification badge on the Header bell showing unread count.
     - Categorized alerts: Academic Notice, Fee Challan Generated, Attendance Alert, Holiday Announcement.
     - Displays formatted timestamp, notification priority badge (`HIGH`, `NORMAL`), and icon.
     - Per-notification "Mark as Read" action triggering `PUT /api/v1/notifications/{id}/read`.
     - "Mark All as Read" action updating the state and refreshing unread badge to 0.
  2. Secured endpoints `GET /api/v1/notifications/my` and `PUT /api/v1/notifications/{id}/read` to ensure users only access and modify their own notifications.
- **Verification Evidence**:
  - Verified in `NotificationAndReportingLifecycleTest.java` (backend JUnit 5).
  - Interactive verification in `Header.tsx`.

---

## Audit Item 6: Fee Payment Recording Modal (Accountant / Admin Only)

- **Component**: `frontend/src/components/modals/RecordPaymentModal.tsx`
- **Observed Defect**: Accountant had no interface to record offline cash or bank payments against pending fee invoices; teachers and students could see payment buttons.
- **Root Cause**: Missing modal for `POST /api/v1/finance/payments` and fee view was not role-scoped.
- **Implementation & Resolution**:
  1. Built `RecordPaymentModal.tsx`:
     - Student selector and pending invoice lookup.
     - Payment mode dropdown: `CASH`, `BANK_TRANSFER`, `ONLINE_1LINK`, `CHEQUE`.
     - Transaction / Challan Reference number input.
     - Payment Amount (PKR) with validation against remaining invoice balance.
     - Payment date and notes fields.
  2. Connected to `POST /api/v1/finance/payments`:
     - Accessible strictly by `ROLE_SUPER_ADMIN`, `ROLE_SCHOOL_ADMIN`, and `ROLE_ACCOUNTANT`.
     - Strictly hidden from teachers and students.
  3. In `FeesPage.tsx`:
     - If logged in as student (`ROLE_STUDENT`): All-school KPI cards are removed, fee structure setup is removed, payment button is removed, and table is filtered strictly to the student's personal invoices (`INV-202610-0104`).
     - If logged in as teacher (`ROLE_TEACHER`): Access is completely blocked (`ForbiddenPage`).
- **Verification Evidence**:
  - Verified in `FeeInvoiceAndPaymentLifecycleTest.java` (backend JUnit 5).
  - Verified in `AuthorizationRbacTest.java` (backend JUnit 5).
  - Verified in Vitest frontend test suite.

---

## Summary of Verification Matrix

| Interactive Feature | Frontend Component | Backend API Endpoint | Tested & Verified |
|---|---|---|:---:|
| Sindh Board Report Card | `ReportCardModal.tsx` | `GET /api/v1/exams/my-report-cards` | **YES** |
| New Admission Application | `NewAdmissionModal.tsx` | `POST /api/v1/admissions/applications` | **YES** |
| Mark Attendance | `MarkAttendanceModal.tsx` | `POST /api/v1/attendance/mark` | **YES** |
| Timetable & Schedule | `StudentDashboard.tsx` | `GET /api/v1/timetable/my` | **YES** |
| Notification Center | `NotificationDrawer.tsx` | `GET /api/v1/notifications/my` & `/read`| **YES** |
| Record Fee Payment | `RecordPaymentModal.tsx` | `POST /api/v1/finance/payments` | **YES** |
