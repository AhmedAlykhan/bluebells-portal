# Bluebells Schooling System — Master Requirement Traceability Matrix

**Project**: Bluebells Schooling System, Hyderabad, Sindh, Pakistan  
**Audit Date**: November 2026  
**Status**: 100% Implemented & Verified  

---

## 1. Requirement to Implementation Traceability

| Req ID | Module & Feature | Backend Controller & Service | Primary DB Tables | Frontend Route / Component | Test Suite & Verification |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **REQ-AUTH-01** | Stateless JWT Authentication & Refresh | `AuthController`, `JwtService`, `UserDetailsServiceImpl` | `users`, `roles`, `user_roles` | `/login`, `LoginPage.tsx` | `AuthenticationIntegrationTest.java` (Passed) |
| **REQ-AUTH-02** | Multi-Role Persona Switcher & RBAC | `SecurityConfig`, `JwtAuthenticationFilter` | `roles`, `permissions` | `Sidebar.tsx`, `App.tsx` | `App.test.tsx` (Passed) |
| **REQ-ADM-01** | Student Admission Inquiry & Verification | `AdmissionController`, `AdmissionService` | `admission_applications`, `guardians` | `/admissions`, `AdmissionsPage.tsx` | `AdmissionToEnrollmentLifecycleTest.java` (Passed) |
| **REQ-ADM-02** | Automated Student Enrollment & Roll Number | `AdmissionController`, `StudentService` | `students`, `enrollments`, `guardians` | `/students`, `StudentsPage.tsx` | `AdmissionToEnrollmentLifecycleTest.java` (Passed) |
| **REQ-ATT-01** | Daily & Subject Student Attendance | `AttendanceController`, `AttendanceService` | `student_attendance`, `class_sections` | `/attendance`, `AttendancePage.tsx` | Full regression suite (Passed) |
| **REQ-ATT-02** | Chronic Absenteeism Flagging & SMS | `AttendanceController`, `NotificationService` | `student_attendance`, `notifications` | `/attendance`, `AnalyticsPage.tsx` | `ManagementAnalyticsTest.java` (Passed) |
| **REQ-FEE-01** | Monthly Fee Invoice Generation (PKR) | `FeeInvoiceController`, `FeeInvoiceService` | `fee_invoices`, `fee_categories` | `/fees`, `FeesPage.tsx` | `FeeInvoiceAndPaymentLifecycleTest.java` (Passed) |
| **REQ-FEE-02** | Official 3-Copy Bank Challan PDF Generation | `FeeInvoicePdfService`, `ReportCardPdfService` | `fee_invoices`, `fee_items` | `/fees`, `ChallanModal.tsx` | Integration testing & manual verification (Passed) |
| **REQ-FEE-03** | Partial Payments, Dues & Scholarships | `FeePaymentController`, `FeePaymentService` | `fee_payments`, `fee_invoices` | `/fees`, `FeesPage.tsx` | `FeeInvoiceAndPaymentLifecycleTest.java` (Passed) |
| **REQ-EXAM-01** | Examinations & Marks Entry | `ExamController`, `MarksEntryService` | `exams`, `marks_entries`, `grading_rules` | `/examinations`, `ExaminationsPage.tsx` | `ExaminationAndReportCardLifecycleTest.java` (Passed) |
| **REQ-EXAM-02** | Sindh Board A3 Official Report Card PDF | `ReportCardService`, `ReportCardPdfService` | `report_cards`, `marks_entries` | `/examinations`, `ReportCardModal.tsx` | `ExaminationAndReportCardLifecycleTest.java` (Passed) |
| **REQ-TIME-01** | Weekly Class Timetable & Clash Engine | `TimetableController`, `TimetableService` | `timetables`, `timetable_periods` | `/timetable`, `TimetablePage.tsx` | `TimetableClashDetectionTest.java` (Passed) |
| **REQ-LMS-01** | Course Catalog & Material Sharing | `CourseController`, `LearningResourceService` | `courses`, `learning_resources` | `/lms`, `LmsPage.tsx` | `LmsPage.test.tsx` (Passed) |
| **REQ-LMS-02** | Homework Submissions & Grading | `AssignmentController`, `AssignmentSubmissionService` | `assignments`, `assignment_submissions` | `/lms`, `LmsPage.tsx` | `LmsPage.test.tsx` (Passed) |
| **REQ-IMP-01** | Bulk Student & Staff Excel/CSV Import | `DataImportController`, `DataImportService` | `import_batches`, `import_records` | `/admin/data-import`, `DataImportPage.tsx` | `DATA_IMPORT_TEST_RESULTS.md` (Passed) |
| **REQ-ANL-01** | Executive Multi-Domain Analytics | `AnalyticsController`, `AnalyticsService` | `students`, `student_attendance`, `fee_invoices` | `/analytics`, `AnalyticsPage.tsx` | `ManagementAnalyticsTest.java` (Passed) |
| **REQ-ANL-02** | Accountant Financial-Only Analytics Isolation | `AnalyticsController`, `AnalyticsService` | `fee_invoices`, `fee_payments` | `/analytics`, `AnalyticsPage.tsx` | `ManagementAnalyticsTest.java`, `AdvancedModules.test.tsx` (Passed) |
| **REQ-ANL-03** | 6-Sheet Leadership vs 2-Sheet Finance Excel Export | `AnalyticsService`, `Apache POI` | `students`, `academics`, `finance` | `/analytics`, `AnalyticsPage.tsx` | `ManagementAnalyticsTest.java` (Passed) |
| **REQ-DEV-01** | Student Co-Curricular & Sports Portfolio | `StudentDevelopmentController`, `StudentDevelopmentService` | `student_development_records` | `/development`, `StudentDevelopmentPage.tsx` | `StudentDevelopmentTest.java`, `AdvancedModules.test.tsx` (Passed) |
| **REQ-DEV-02** | Formal Approval Lifecycle & Evidence Storage | `StudentDevelopmentController`, `StudentDevelopmentService` | `student_development_records` | `/development`, `StudentDevelopmentPage.tsx` | `StudentDevelopmentTest.java` (Passed) |
| **REQ-IDC-01** | Printable Smart Student Identity Cards | `StudentIdCardController`, `StudentIdCardService` | `student_id_cards` | `/id-cards`, `StudentIdCardsPage.tsx` | `StudentIdCardTest.java`, `AdvancedModules.test.tsx` (Passed) |
| **REQ-IDC-02** | High-Entropy Cryptographic QR Verification | `StudentIdCardService`, `QrCodeService` | `student_id_cards` | `/verify-id`, `PublicVerifyIdCardPage.tsx` | `StudentIdCardTest.java` (Passed) |
| **REQ-CRT-01** | Official School Leaving & Merit Certificates | `CertificateController`, `CertificateService` | `certificates`, `certificate_templates` | `/certificates`, `CertificatesPage.tsx` | `CertificateServiceTest.java`, `AdvancedModules.test.tsx` (Passed) |
| **REQ-CRT-02** | Template Sanitization & Mandatory Revocation | `CertificateController`, `CertificateService` | `certificates` | `/certificates`, `CertificatesPage.tsx` | `CertificateServiceTest.java` (Passed) |
| **REQ-SUP-01** | Parent & Student Help Desk Ticketing | `SupportTicketController`, `SupportTicketService` | `support_tickets`, `ticket_messages` | `/support`, `SupportCentrePage.tsx` | `SupportTicketPrivacyTest.java`, `AdvancedModules.test.tsx` (Passed) |
| **REQ-SUP-02** | Strict Parent Privacy & Confidential Notes | `SupportTicketController`, `SupportTicketService` | `support_tickets`, `ticket_messages` | `/support`, `SupportCentrePage.tsx` | `SupportTicketPrivacyTest.java` (Passed) |
| **REQ-INT-01** | Enterprise Integrations Console | `SchoolIntegrationController`, `SchoolIntegrationService` | `school_integrations` | `/integrations`, `IntegrationsPage.tsx` | `SchoolIntegrationTest.java`, `AdvancedModules.test.tsx` (Passed) |
| **REQ-INT-02** | Credential Masking & Heartbeat Ping | `SchoolIntegrationController`, `SchoolIntegrationService` | `school_integrations` | `/integrations`, `IntegrationsPage.tsx` | `SchoolIntegrationTest.java` (Passed) |
| **REQ-AST-01** | Factual Context-Aware AI School Assistant | `SchoolAssistantController`, `SchoolAssistantService` | Dynamic grounding across LMS & Portal | Floating Assistant Widget, `VirtualAssistant.tsx` | `SchoolAssistantTest.java` (Passed) |
