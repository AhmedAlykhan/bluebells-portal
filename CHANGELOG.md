# Changelog — Bluebells School LMS & School Management Portal

All notable changes and deliverables for Bluebells Schooling System, Hyderabad, Sindh, Pakistan are documented here.

## [1.1.0] — 2026-11-04 (Security Hardening, Live API Integration & DTO Type Safety)

### Backend Enhancements:
- **Financial Route Completeness**: Added `getAllInvoices(status, billingMonth)` and `getAllPayments()` in `FeeService.java` and exposed them via `@GetMapping("/invoices")` and `@GetMapping("/payments")` in `FeeController.java` with role authorization (`SUPER_ADMIN`, `SCHOOL_ADMIN`, `PRINCIPAL`, `ACCOUNTANT`).
- **Strictly Typed Enrollment Payloads**: Created `EnrollStudentRequest.java` DTO with `@NotNull`, `@NotBlank`, and validation constraints, eliminating unchecked casts and raw `Map<String, Object>` payloads in `StudentController.enrollStudent`.
- **Credential Rotation Validation**: Added password reuse detection in `AuthService.changePassword` to reject attempts to update a password to the user's current password.
- **Account Lock Enforcement**: Hardened `JwtAuthenticationFilter` and `UserPrincipal` to immediately reject JWT requests from locked or disabled accounts.
- **Formula Injection Defense & Export Access**: Added `ACCOUNTANT` role to CSV export endpoints in `ReportController.java` while enforcing OWASP/CWE-1236 prefix sanitization.
- **N+1 Query Elimination**: Added `@EntityGraph` loading for student relationships in `StudentRepository.java`.

### Frontend Enhancements:
- **Live Data Synchronization**:
  - `AccountantDashboard.tsx`: Connected real-time payment transactions via `GET /api/v1/finance/payments` with instant table refresh upon recording payments.
  - `StudentDashboard.tsx`: Dynamically queries `GET /api/v1/students/me` to populate student profile details (admission number, roll number, class) dynamically.
  - `ParentDashboard.tsx`: Dynamically queries `GET /api/v1/guardians/my-children` for multi-child profile switching.
  - `ReportsPage`: Connected to live institutional KPIs via `GET /api/v1/reports/dashboard`.
- **Sanitized CSV Report Exports**:
  - Added dedicated export triggers in `ModulePages.tsx` for Students Roster (`/reports/students/export-csv`), Daily Attendance Log (`/reports/attendance/export-csv`), and Fee Ledger (`/reports/fees/export-csv`).
- **Live Search & Dynamic Invoicing**:
  - Implemented live student directory search with real-time backend query fetching in `StudentsPage`.
  - Implemented dynamic invoice ledger loading in `FeesPage` based on authenticated user role.

## [1.0.0] — 2026-10-28 (Release Candidate)

### Block 0: Environment Inspection & Project Plan
- Inspected local Apple Silicon macOS toolchain (Java 17, Node.js 22, MySQL 8.x/26.x).
- Created system architecture documentation (`ARCHITECTURE.md`).
- Designed 40-table normalized relational schema (`DATABASE_DESIGN.md`).
- Defined Role-Permission Matrix across 7 roles and 16 permissions (`ROLE_PERMISSION_MATRIX.md`).
- Authored comprehensive threat model (`THREAT_MODEL.md`) and testing strategy (`TESTING_STRATEGY.md`).
- Mapped all requirements to engineering specifications (`REQUIREMENTS_TRACEABILITY.md`).

### Block 1: Repository & Application Foundations
- Initialized Spring Boot 3.3.5 backend with Maven settings mirror configuration.
- Created Flyway database migrations `V1__initial_schema.sql` (40 tables) and `V2__seed_baseline_data.sql`.
- Built responsive Vite + React 18 + TypeScript + Tailwind CSS portal frontend shell.
- Established unified verification runner (`scripts/verify-all.sh`).

### Block 2: Authentication & Authorization
- Implemented JWT token provider with HMAC-SHA512.
- Configured BCrypt password hashing at cost factor 12.
- Implemented login rate limiting (5 attempts/min) and IP throttling.
- Implemented Spring Security RBAC filter chain and RFC 7807 problem details handler.
- Verified with 9 automated security integration tests.

### Block 3: School Setup & Academic Structure
- Implemented academic sessions, terms, grade levels, classes (`ClassEntity`), sections, and subjects.
- Added teacher staff profiles and weekly period assignments.
- Implemented `AcademicController` with fine-grained role authorization.
- Verified with 16 automated integration tests.

### Block 4: Admissions, Students, Guardians, Enrollment
- Implemented online admission applications (`APP-YYYY-XXXX`).
- Implemented one-click student enrollment conversion (`BSS-YYYY-XXXX`).
- Implemented multi-child guardian linking with row-level parent isolation.
- Verified with 18 automated integration tests.

### Block 5: Timetable & Attendance
- Implemented weekly section timetable with 3-way clash detection queries (teacher overlap, section overlap, room conflict).
- Implemented bulk daily section attendance marking with duplicate rejection (409 Conflict).
- Implemented attendance correction with mandatory audit justification logging.
- Implemented parent monthly attendance summaries.
- Verified with 20 automated integration tests.

### Block 6: LMS & Coursework
- Implemented course creation linked to classes and subjects.
- Implemented sequential lessons with markdown rendering.
- Implemented assignment distribution with deadline enforcement (late submissions rejected with 400 Bad Request).
- Implemented student submission and teacher grading with boundary validation.
- Verified with 21 automated integration tests.

### Block 7: Examinations & Results
- Implemented examination scheduling linked to academic terms.
- Implemented batch marks recording with boundary checks (marks > maxMarks returns 400).
- Implemented result publication immutability (locks exam against post-publish edits).
- Implemented automated grading scale lookup according to Sindh Board criteria (A+, A, B, C, D, F).
- Implemented automated class rank calculation based on overall percentage.
- Implemented row-level parent isolation for report card viewing.
- Verified with 22 automated integration tests.

### Block 8: Fees & Finance
- Implemented class-level fee structures (Monthly Tuition, Admission, Annual charges).
- Implemented batch recurring monthly fee invoice generation.
- Enforced zero floating-point arithmetic using `BigDecimal` (`DECIMAL(12,2)`).
- Implemented partial payment receipts with automatic FIFO allocation across unpaid invoices.
- Implemented overpayment protection against fully settled invoices.
- Implemented row-level parent isolation for fee ledgers and payment receipts.
- Verified with 23 automated integration tests.

### Block 9: Notifications, Reports & Analytics
- Implemented in-app notification system with read receipts and unread badge counting.
- Implemented outgoing email notification adapter with Mailpit compatibility.
- Implemented executive KPI dashboard summarizing students, staff, attendance rate, and fees.
- Implemented CSV export endpoints for students, attendance, and fees with formula injection protection (CWE-1236).
- Verified with 24 automated integration tests.

### Block 10: Performance & Security Hardening
- Implemented performance benchmark suite verifying p95 read latency SLA < 18ms (target < 300ms).
- Verified SQL injection resilience against hostile SQL payloads.
- Verified CWE-1236 CSV formula injection defense matrix.
- Implemented transactional database backup (`scripts/db-backup.sh`) and restore verification drill (`scripts/db-restore-test.sh`) with 100% table and row parity.
- Verified with 27 automated integration tests.

### Block 11: Release Candidate & Handover
- Built self-contained production backend JAR (`59M`).
- Built production frontend distribution (`274 kB`).
- Authored release certification report (`RELEASE_READINESS.md`).
- Authored comprehensive role-based operations manual (`USER_MANUAL.md`).
- Updated project status and master README.
