# AGENT.md — Engineering & AI Pair-Programming Guide

> **Target Audience:** Autonomous coding agents, AI pair programmers (Antigravity, Codex, Claude Code), and human software engineers maintaining the **Bluebells School LMS & School Management Portal**.

---

## 1. System Mission & Context

- **Institution:** Bluebells Schooling System
- **Location:** Latifabad Unit 2, Adjacent Autobahn Road, Next to VIP Garden, Hyderabad, Sindh, Pakistan (71800)
- **Domain:** Comprehensive K-12 School Management & Learning Management System (LMS)
- **Primary Stakeholders:** Super Administrators, Principals, Teachers, Accountants, Parents/Guardians, and Students.
- **Nature of Project:** Real-world, production-quality, modular monolith with high security, strict RBAC, and deterministic data isolation.

---

## 2. Technology Stack & Local Environment

| Layer | Technologies & Frameworks | Version / Constraints |
| :--- | :--- | :--- |
| **Backend** | Java (OpenJDK 17 LTS), Spring Boot, Spring Security, Spring Data JPA | 3.3.5 |
| **Database** | MySQL 8.x, Flyway versioned migrations | InnoDB, utf8mb4 |
| **Frontend** | React 18, TypeScript 5, Vite 5, Tailwind CSS, Lucide Icons | Single-page application |
| **Testing** | JUnit 5, Spring MockMvc, Testcontainers, Vitest, Testing Library | Strict 100% pass requirement |
| **Security** | JWT (HMAC-SHA256), BCrypt (cost factor 12), RFC 7807 Problem Details | Stateless auth |

### Local Environment Peculiarities
- **Java 17 Path on macOS:** `/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home` (or `/opt/homebrew/Cellar/openjdk@17/17.0.20.1/libexec/openjdk.jdk/Contents/Home`).
- **Maven Execution:** In local development, Maven may rely on a local mirror settings file:
  ```bash
  JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home \
  mvn -o -s backend/settings.xml test -f backend/pom.xml
  ```
- **Database Loopback:** Running backend tests requires local MySQL access on `127.0.0.1:3306`. If running inside a terminal sandbox, invoke commands with `BypassSandbox: true`.

---

## 3. Core Architectural Rules & Non-Negotiable Invariants

### 3.1 Authentication & Session Handling
- **No Mock Admin Fallbacks:** The frontend must **never** fall back to a default mock admin user when unauthenticated. If `token` or `bluebells_user` is missing or invalid, `user` must remain `null` and `<LoginPage />` must be displayed.
- **Account Lock Checks:** If a user account has `lockedUntil` set in the future, both `UserPrincipal.isAccountNonLocked()` and `JwtAuthenticationFilter` must reject incoming requests with HTTP 403 Forbidden.

### 3.2 Authorization & Row-Level Data Isolation (RBAC)
- **Students:** Must only access student-scoped APIs (`/api/v1/lms/my`, `/api/v1/attendance/my`, `/api/v1/timetable/my`, etc.). Students must never have access to administrative, financial, or student management routes.
- **Parents / Guardians:** Row-level isolation is enforced via `authorizationService.checkStudentAccess(studentId, principal)`. A parent can only view records for students explicitly linked to them in the `guardians` / `guardian_student_links` table. Cross-family queries must return HTTP 403 Forbidden.
- **Teachers:** Teachers can only grade submissions, record attendance, and manage marks for sections and courses they are actively assigned to via `teacher_assignments`. Cross-teacher tampering must return HTTP 403 Forbidden.
- **Accountants:** Have explicit permissions for fee structures, invoices, payment recording, and financial report exports (`/api/v1/reports/fees/export-csv`).

### 3.3 Database Integrity & Concurrency
- **Schema Migrations:** Schema changes must **only** be introduced via versioned Flyway migrations in `backend/src/main/resources/db/migration/` (e.g., `V17__description.sql`). **Never edit existing applied migrations.**
- **Sequence Generation:** Avoid in-memory atomic counters (`AtomicLong`) that reset to 0 upon JVM restart. Always use collision-resilient generation routines with database existence checks (`generateUniqueInvoiceNumber`, `generateUniquePaymentReference`, `existsByInvoiceNumber`, `existsByPaymentReference`).
- **N+1 Query Prevention:** Always annotate entity relationships or repository methods with `@EntityGraph` (or `JOIN FETCH`) when listing parent-child hierarchies (e.g., `StudentRepository.findAll` and `searchStudents` eagerly fetching `enrollments`, `section`, and `classEntity`).
- **Financial Bounds:** Invoices and payments must enforce strict bounds:
  - Discounts cannot exceed total invoice amounts.
  - Payment amounts cannot exceed invoice balance due or total outstanding student balance.
  - Invoices must maintain accurate status: `UNPAID`, `PARTIALLY_PAID`, or `PAID`.

### 3.4 Error Handling & API Contracts
- All error responses must adhere strictly to **RFC 7807 Problem Details** via `GlobalExceptionHandler.java`:
  - `status`: HTTP status code (int)
  - `title`: Short error title (string)
  - `detail`: User-facing explanation (string)
  - `instance`: URI path of request
  - `timestamp`: ISO-8601 timestamp
  - `errorId`: UUID for tracing in application logs
- Standard Spring MVC exceptions (`HttpRequestMethodNotSupportedException`, `HttpMessageNotReadableException`, `MethodArgumentTypeMismatchException`, `NoResourceFoundException`) must be explicitly handled and return clean 400/404/405 responses rather than generic 500 internal server errors.

### 3.5 Test Resilience & Date Handling
- **Never Hardcode Current Calendar Dates:** Tests that mark attendance, generate invoices, or filter by month must use `LocalDate.now()` rather than a hardcoded past date. This prevents test failures when the calendar month rolls over.

---

## 4. Common Tasks & Commands

### 4.1 Running All Quality Gates
```bash
# Backend test verification (all 38 integration tests)
JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home \
mvn -o -s backend/settings.xml test -f backend/pom.xml

# Frontend unit & RBAC tests (5 tests)
npm --prefix frontend test -- --run

# Frontend TypeScript check and production build
npm --prefix frontend run build
```

### 4.2 Starting and Stopping Local Services
```bash
# Start MySQL-backed stack
./scripts/start-local.sh

# Stop all local background listeners (ports 8080 & 5173)
./scripts/stop-local.sh
```

### 4.3 Database Backup & Restore Drills
```bash
# Create transactional mysqldump with SHA256 checksum
./scripts/db-backup.sh

# Test restoration against an isolated drill database
./scripts/db-restore-test.sh
```

---

## 5. Directory Map for Agents

| Directory | Purpose & Guidance for Agents |
| :--- | :--- |
| `backend/src/main/java/com/bluebells/security/` | Core security: `SecurityConfig`, `JwtAuthenticationFilter`, `UserPrincipal`, `AuthorizationService`. Modify when adjusting auth policies. |
| `backend/src/main/java/com/bluebells/exception/` | Error models and `GlobalExceptionHandler`. Keep clean RFC 7807 mappings. |
| `backend/src/main/java/com/bluebells/finance/` | Fee invoicing, payment allocations, receipts. Verify collision resilience on any changes. |
| `backend/src/main/java/com/bluebells/examination/` | Exams, grading scale, report card generation, class rank partitioning. |
| `frontend/src/context/AuthContext.tsx` | JWT lifecycle, login/logout, persona switching. Must never contain default admin bypasses. |
| `frontend/src/api/client.ts` | Axios instance with JWT interceptor and 401 redirect logic. |
| `frontend/src/App.tsx` | React Router definitions, role-protected layouts (`StudentLayout`, `ParentLayout`, `StaffLayout`). |
| `scripts/` | Shell scripts for local development operations and quality assurance. |
