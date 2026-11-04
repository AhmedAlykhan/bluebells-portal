# Requirements Traceability Matrix — Bluebells School LMS

| Req ID | Requirement Description | Module | Primary Components / Endpoints | Verification & Test Suite |
|---|---|---|---|---|
| **REQ-A1** | Secure authentication, password hashing, token issue | Auth | `AuthController`, `JwtService`, `CustomUserDetailsService` | `AuthControllerTest`, `SecurityConfigTest` |
| **REQ-A2** | Role & resource authorization, denial by default | Auth/RBAC | `@PreAuthorize`, `SecurityFilterChain`, `MethodSecurity` | `AuthorizationSecurityTest`, `IdorProtectionTest` |
| **REQ-B1** | Configurable school profile, timezone, PKR currency | School | `SchoolProfileController`, `SchoolProfileService` | `SchoolProfileServiceTest`, `SchoolProfileControllerTest` |
| **REQ-B2** | Academic sessions, terms, classes, sections, subjects | Academics | `AcademicSessionController`, `ClassController`, `SubjectController` | `AcademicsIntegrationTest` |
| **REQ-C1** | Admission workflow, inquiry tracking, admission numbering | Admissions | `AdmissionController`, `AdmissionApplicationService` | `AdmissionWorkflowTest` |
| **REQ-C2** | Student registration, B-Form, guardian links, enrollment | Students | `StudentController`, `GuardianController`, `EnrollmentService` | `StudentLifecycleTest`, `GuardianIsolationTest` |
| **REQ-D1** | Teacher class/subject assignments, class rosters | Staff | `StaffController`, `TeacherAssignmentService` | `TeacherAssignmentTest` |
| **REQ-D2** | Timetable management and clash detection | Timetable | `TimetableController`, `TimetableService` | `TimetableClashDetectionTest` |
| **REQ-E1** | Daily attendance recording, duplicate prevention | Attendance | `AttendanceController`, `AttendanceService` | `AttendanceDuplicateConstraintTest` |
| **REQ-E2** | Attendance correction workflow and audit | Attendance | `AttendanceCorrectionService`, `AuditLogRepository` | `AttendanceCorrectionAuditTest` |
| **REQ-F1** | LMS courses, lessons, assignments, deadlines | LMS | `CourseController`, `AssignmentController` | `AssignmentLifecycleTest` |
| **REQ-F2** | Student submission, grading, teacher feedback | LMS | `SubmissionController`, `SubmissionService` | `SubmissionGradingTest` |
| **REQ-G1** | Examination schedules, marks entry, grade scale | Exams | `ExamController`, `MarksEntryController` | `GradingScaleCalculationTest` |
| **REQ-G2** | Published results immutability, report cards | Exams | `ReportCardService`, `ExamResultController` | `ReportCardGenerationTest` |
| **REQ-H1** | Configurable fee structures, monthly/term billing | Fees | `FeeStructureController`, `InvoiceGenerationService` | `FeeBillingCalculationTest` |
| **REQ-H2** | Payment recording, allocations, receipt generation | Fees | `PaymentController`, `PaymentAllocationService` | `FinancialLedgerInvariantsTest` |
| **REQ-I1** | Parent portal, multi-child switching, isolation | Portals | `ParentPortalController`, `GuardianService` | `ParentChildIsolationE2ETest` |
| **REQ-I2** | Student portal, homework, timetable, report card | Portals | `StudentPortalController` | `StudentPortalE2ETest` |
| **REQ-J1** | In-app notifications, read/unread states | Notifications | `NotificationController`, `NotificationService` | `NotificationServiceTest` |
| **REQ-K1** | Secure file upload, download authorization, CSV export | Documents | `DocumentController`, `CsvExportService` | `SecureDocumentDownloadTest`, `CsvFormulaInjectionTest` |
| **REQ-L1** | Administrative dashboards and query aggregation | Analytics | `AnalyticsController`, `AnalyticsQueryService` | `AnalyticsAggregationPerformanceTest` |
