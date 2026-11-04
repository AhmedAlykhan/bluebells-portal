# Testing Strategy & Quality Gates — Bluebells School LMS

## 1. Testing Philosophy & Automated Pyramid

Our testing strategy follows the principle of **"Test Everything That Matters"**—no feature is considered complete until automated tests execute against real implementations, verify expected behavior, test failure boundaries, and record actual passing results.

```
          / \
         / E2E \       Playwright User Journeys (Admin, Teacher, Parent, Student)
        /-------\
       / Integr. \     Spring Boot Test, MockMvc, Repository, Flyway on MySQL
      /-----------\
     /  Unit Tests \   JUnit 5, Mockito, AssertJ, Domain Logic, Vitest (Frontend)
    /---------------\
```

---

## 2. Test Suites by Layer

### Layer A: Backend Unit Tests (JUnit 5 + AssertJ)
- **Domain Invariants**:
  - Academic promotion logic (pass/fail criteria, attendance minimum thresholds).
  - Examination grade scale resolution (percentage to grade point mapping).
  - Financial ledger balance calculations, partial payment allocations, discount validation.
  - Guardian-student link authorization logic.
- **Validation Rules**:
  - Bean validation constraints (`@Valid`, custom validators).
  - Date ordering (session start before end, admission date not in future).

### Layer B: Backend Integration Tests (`@SpringBootTest` + `MockMvc`)
- **Database Migrations**:
  - Flyway executes cleanly from scratch to latest version on a clean test schema.
- **Persistence & Repositories**:
  - Complex custom queries, projections, pagination with stable sorting.
  - Optimistic locking collision verification (`@Version` throwing `OptimisticLockingFailureException`).
- **HTTP Security & API Contracts**:
  - Role-based authorization: verifying 200 OK for authorized roles and 403 Forbidden for unauthorized roles.
  - Resource isolation: cross-child guardian access rejection, cross-class teacher marks rejection.
  - Centralized RFC 7807 error format verification.

### Layer C: Frontend Component & State Tests (Vitest + RTL)
- Form validation: Zod schema enforcement for required fields and invalid formats.
- API state handling: Loading skeletons, error banners with retry, empty table placeholders.
- Role-based navigation: ensuring menus adapt to active role permissions.

### Layer D: End-to-End User Journeys (Playwright)
- Synthetic personas:
  - `admin@bluebells.edu.pk` (SCHOOL_ADMIN)
  - `principal@bluebells.edu.pk` (PRINCIPAL)
  - `teacher.ahmed@bluebells.edu.pk` (TEACHER)
  - `parent.khan@bluebells.edu.pk` (PARENT)
  - `student.ali@bluebells.edu.pk` (STUDENT)
  - `accountant@bluebells.edu.pk` (ACCOUNTANT)
- Full lifecycle tests:
  1. Admin configures Academic Session 2026-2027 and adds Class 9 Section A.
  2. Admin admits Student Ali and links Parent Khan.
  3. Teacher marks attendance for Class 9 Section A.
  4. Parent Khan logs in and views Ali's attendance.
  5. Teacher creates an assignment; Student Ali submits homework; Teacher grades with feedback.
  6. Accountant records tuition payment; Parent views updated invoice balance and receipt.

### Layer E: Performance & Load Testing
- Open-source k6 script exercising paginated student reads, attendance submissions, and dashboard metrics with staged concurrency (10, 25, 50 virtual users).

---

## 3. Quality Gates & Thresholds

| Metric | Target Gate | Enforcement Tool |
|---|---|---|
| Backend Line Coverage | >= 80% | JaCoCo Maven Plugin |
| Critical Branch Coverage (Finance/Auth/Grading) | >= 90% | JaCoCo Maven Plugin |
| Frontend Line Coverage | >= 80% | Vitest v8 Coverage |
| Static Code Analysis | 0 critical/high bugs | SpotBugs / SonarLint / ESLint |
| TypeScript Type Safety | 0 type errors | `tsc --noEmit` |
| Security Vulnerabilities | 0 known CVEs (High/Critical) | Maven Dependency Check / npm audit |
