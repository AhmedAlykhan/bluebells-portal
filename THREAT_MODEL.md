# Threat Model (STRIDE) — Bluebells School LMS

## 1. Scope & System Assets
The threat model analyzes potential adversarial threats targeting Bluebells School LMS, covering:
- **Critical Assets**:
  - Student and guardian PII (B-Forms, addresses, telephone contacts).
  - Financial ledger entries, tuition balances, and payment receipts.
  - Academic examination marks, grading calculations, and official report cards.
  - User authentication credentials and active authorization tokens.
  - Student homework submissions and uploaded verification documents.

---

## 2. STRIDE Threat Analysis & Mitigations

| Category | Threat Description | Attack Vector / Scenario | Severity | Mitigation & Verification |
|---|---|---|:---:|---|
| **S**poofing | Attacker impersonates an administrator, teacher, or parent | Stolen token, session hijacking, or credential stuffing on `/api/v1/auth/login`. | **High** | Adaptive BCrypt hashing (cost 12), rate-limiting login attempts, short-lived tokens, explicit token revocation. |
| **T**ampering | Unauthorized modification of grades, attendance, or fees | Malicious student alters marks on `/api/v1/examinations/marks` or accountant deletes historical payment. | **Critical** | Server-side RBAC + method security (`@PreAuthorize`), immutable financial ledger with reversal records, optimistic locking (`@Version`). |
| **R**epudiation | User denies recording incorrect attendance or modifying grades | Teacher changes attendance or accountant adjusts fee invoice without record. | **Medium** | Append-only `audit_logs` capturing `principal_username`, action, entity ID, previous/new state, and timestamp. |
| **I**nformation Disclosure | Unauthorized access to other students' report cards or records (IDOR) | Parent queries `/api/v1/students/{id}/report-card` replacing student ID with another child's ID. | **High** | Resource authorization interceptor verifying parent-child link in `guardian_student_links`. |
| **D**enial of Service | Exhaustion of database or server memory through unbounded queries | Scraping collection endpoints or uploading 1GB files to fill disk. | **Medium** | Mandatory pagination (`Pageable`), bounded page size (`max=100`), 10MB upload limits with disk quotas. |
| **E**levation of Privilege | Normal student or teacher assumes `SCHOOL_ADMIN` role | Modifying role in JWT or passing `roles=['SUPER_ADMIN']` in user update request. | **Critical** | Server-side role assignment only; role changes restricted to `SUPER_ADMIN`. Mass-assignment protected via dedicated Request DTOs. |

---

## 3. High-Priority Abuse Scenarios & Verification Tests

1. **Abuse Scenario 1: Multi-Child Horizontal Privilege Escalation**
   - *Test*: Parent A (linked to Student 1) attempts to query `/api/v1/students/2/attendance`.
   - *Expected Result*: `HTTP 403 Forbidden` with RFC 7807 problem details.
2. **Abuse Scenario 2: Student Grade Tampering**
   - *Test*: Student attempts to POST/PUT to `/api/v1/examinations/{id}/marks`.
   - *Expected Result*: `HTTP 403 Forbidden`.
3. **Abuse Scenario 3: Unbounded Export DoS**
   - *Test*: Requesting `/api/v1/students?size=1000000`.
   - *Expected Result*: Page size capped at maximum configured limit (e.g. 50 or 100).
4. **Abuse Scenario 4: Spreadsheet Formula Injection**
   - *Test*: Student registered with name `=cmd|' /C calc'!A0`.
   - *Expected Result*: CSV export escapes leading `=` with a single quote `'=cmd|' /C calc'!A0`.
