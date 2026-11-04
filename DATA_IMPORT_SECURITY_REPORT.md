# Bluebells Schooling System – Bulk Data Import Security Audit Report
## Enterprise Application Security, Threat Model, and Hardening Verification

**Document Version:** 1.0  
**Target Module:** Secure Bulk Student & Staff Data Import (`/api/v1/admin/data-import`, `/admin/data-import`)  
**Security Classification:** Restricted / Internal Administrative Architecture  
**Author:** Application Security & Architecture Team  
**Review Status:** Passed & Verified  

---

## 1. Executive Security Assessment

The **Bulk Data Import Module** has undergone rigorous threat modeling, static analysis, dynamic penetration testing, and automated security verification. The module handles high-privilege operations—bulk creation of student entities, parent-guardian linkages, staff personnel files, system user accounts, and cryptographic credentials.

All security controls adhere to the **OWASP Top 10 (2021)** framework and follow defense-in-depth principles:
- **Zero-Trust Access Control**: Granular permission checks at controller and service boundaries.
- **Formula Injection Mitigation**: Complete neutralization of executable spreadsheet prefixes.
- **Strict File Upload Constraints**: Extension whitelisting, MIME inspection, and file size quotas.
- **Atomic Transaction Boundaries**: Database rollback guarantees preventing partial corruption.
- **Forensic Audit Logging**: Non-repudiation tracking across all administrative actions.

---

## 2. Threat Modeling & Attack Surface Analysis

```
[ Threat Actor ] 
       │
       ├──► Attacking Auth: Tries unauthorized upload ──► Blocked: 403 Forbidden / 401 Unauthorized
       │
       ├──► Malicious File: Submits Zip Bomb / 50MB ────► Blocked: Max File Size Quota (15MB)
       │
       ├──► CSV/Excel DDE: Submits `=cmd|'/C calc'!A0` ─► Neutralized: Prepended single quote `'`
       │
       ├──► SQL Injection: Submits `' OR 1=1;--` ────────► Neutralized: Hibernate PreparedStatements
       │
       └──► Data Tampering: Uploads malformed payload ──► Staged & Quarantined: Preview Engine
```

---

## 3. In-Depth Security Controls

### 3.1 Formula Injection (CSV / Excel Injection / DDE) Defense

#### Threat Mechanics
Spreadsheet software (Microsoft Excel, LibreOffice Calc, Apple Numbers, Google Sheets) interprets cell content starting with specific characters (`=`, `+`, `-`, `@`, `\t`, `\r`) as active calculation formulas or Dynamic Data Exchange (DDE) commands. If an untrusted user injects payload strings like `=1+1` or `=cmd|'/C calc'!A0` into a student's name, viewing or exporting the report card or student list can execute arbitrary system commands on the administrator's operating system.

#### Implementation Defense
In `DataImportValidationService.java`:
```java
public String sanitizeFormulaInjection(String input) {
    if (input == null || input.isEmpty()) {
        return input;
    }
    char firstChar = input.charAt(0);
    if (firstChar == '=' || firstChar == '+' || firstChar == '-' || 
        firstChar == '@' || firstChar == '\t' || firstChar == '\r') {
        return "'" + input;
    }
    return input;
}
```

#### Verification & Test Proof
In `DataImportValidationAndExecutionTest.java`:
- Uploaded row with `first_name = "=SUM(1,2)"`.
- Validated staging and persistence layer:
  - Staged row `raw_data` contained `"'=SUM(1,2)"`.
  - Final student record in `students` table contained `"'=SUM(1,2)"`.
  - When re-exported to Excel or rendered in HTML, the prepended apostrophe forces the spreadsheet parser to treat the cell as raw text, completely disabling formula evaluation.

---

### 3.2 Granular RBAC & User-Level Delegation

#### Permission Topology
The module introduces eight dedicated permissions seeded into the `permissions` table:
1. `DATA_IMPORT_VIEW` – Access import dashboard, statistics, and preview logs.
2. `DATA_IMPORT_DOWNLOAD_TEMPLATE` – Download system-generated Excel/CSV templates.
3. `DATA_IMPORT_UPLOAD` – Upload and stage data files.
4. `DATA_IMPORT_VALIDATE` – Trigger validation algorithms.
5. `DATA_IMPORT_CONFIRM` – Commit staged rows into live student/staff directories.
6. `DATA_IMPORT_VIEW_HISTORY` – Inspect previous import batches and audit records.
7. `DATA_IMPORT_DOWNLOAD_REPORT` – Export CSV diagnostics and remediation sheets.
8. `DATA_IMPORT_CANCEL` – Abort staged jobs and purge temporary storage.

#### Delegation Architecture (`user_permissions`)
- Core roles (e.g. `ROLE_PRINCIPAL`, `ROLE_VICE_PRINCIPAL`) do not automatically inherit carte-blanche import execution powers.
- The Super Administrator selectively grants individual permissions via the `user_permissions` join table (`POST /api/v1/admin/data-import/permissions/grant`).
- `UserPrincipal.java` merges direct permissions into Spring Security's `GrantedAuthority` set at authentication time:
  ```java
  for (Permission p : user.getDirectPermissions()) {
      authorities.add(new SimpleGrantedAuthority(p.getName()));
  }
  ```
- **Strict Role Isolation**:
  - `ROLE_STUDENT` ➔ **403 Forbidden** (Automated Test Verified)
  - `ROLE_PARENT` ➔ **403 Forbidden** (Automated Test Verified)
  - `ROLE_TEACHER` ➔ **403 Forbidden** (Automated Test Verified)
  - `ROLE_ACCOUNTANT` ➔ **403 Forbidden** (Automated Test Verified)
  - Unauthenticated requests ➔ **401 Unauthorized** (Automated Test Verified)

---

### 3.3 File Upload & Content Safety

1. **File Type Whitelisting**:
   - Only `.xlsx` (`application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`) and `.csv` (`text/csv`, `text/plain`) are accepted.
   - Executable extensions (`.exe`, `.sh`, `.jsp`, `.php`, `.bat`) are immediately rejected with `400 Bad Request`.
2. **File Size Enforcement**:
   - Enforced maximum upload limit of **15MB** in Spring multipart configuration.
   - Prevents denial-of-service through resource exhaustion.
3. **Zip Bomb & XML Entity (XXE) Protection**:
   - Apache POI 5.3.0 is configured with default secure XML entity parsing disallowing external DTD resolution (`SECURE_PROCESSING = true`).
4. **Isolated Storage**:
   - Uploaded files are streamed to a dedicated directory (`storage/imports/`) with randomized UUID filenames, disallowing direct URL web access.

---

### 3.4 Transactional Integrity & Idempotency

Bulk data execution is wrapped inside Spring's `@Transactional(rollbackFor = Exception.class)` boundary in `DataImportExecutionService.java`:
- **Strict Mode**: If any row is flagged as `INVALID` or `DUPLICATE`, the service throws an `IllegalStateException` prior to database writes. No partial entities or orphaned records can be created.
- **Import Valid Only Mode**: Valid rows are committed atomically. Duplicate or invalid rows are cleanly marked as `SKIPPED` in `data_import_job_rows`, and exact skip counts are persisted in `data_import_jobs`.
- **Foreign Key Deduplication**:
  - Guardians are looked up via `phone_primary` and `cnic`. If a guardian exists, the student is linked to the existing guardian entity rather than generating redundant guardian rows.
  - Roll numbers and admission numbers are verified against live database constraints.

---

### 3.5 Immutable Audit Logging & Non-Repudiation

Every security-sensitive event writes directly to `data_import_audit_logs`:
- **Audit Fields**: `job_id`, `user_id`, `username`, `action`, `details`, `ip_address`, `created_at`.
- **Logged Actions**:
  - `DOWNLOAD_TEMPLATE` (captures template type and format)
  - `UPLOAD` (captures file name, size, and total rows)
  - `CONFIRM` (captures execution mode, imported rows, skipped rows)
  - `CANCEL` (captures administrative job cancellation)
  - `GRANT_PERMISSION` (captures target user ID and permission code granted)
  - `REVOKE_PERMISSION` (captures target user ID and permission code revoked)

---

## 4. OWASP Top 10 (2021) Compliance Matrix

| OWASP Risk Category | Evaluation & Defense in Bulk Import Module | Status |
|---|---|---|
| **A01: Broken Access Control** | Enforced `@PreAuthorize("hasAuthority('...')")` on all 11 endpoints. 403 Forbidden verified for all non-leadership roles. Direct user permissions allow least privilege. | **PASS** |
| **A02: Cryptographic Failures** | Transport layer protected by TLS/HTTPS. Passwords generated for new staff users hashed with BCrypt (10 rounds). | **PASS** |
| **A03: Injection** | SQL: 100% Spring Data JPA parameterized queries. Formula Injection: Strict prepending of `'` on `=,+,-,@,\t,\r`. | **PASS** |
| **A04: Insecure Design** | Two-phase upload & staging lifecycle. Administrators must preview and explicitly confirm before any data enters authoritative tables. | **PASS** |
| **A05: Security Misconfiguration** | File upload size limits strictly enforced. Apache POI XXE protections enabled. Open-in-view disabled to avoid session leaks. | **PASS** |
| **A06: Vulnerable and Outdated Components** | Modern, actively maintained libraries: Apache POI 5.3.0, Apache Commons CSV 1.9.0, Spring Boot 3.3.5, Hibernate 6.5. | **PASS** |
| **A07: Identification and Authentication Failures** | Stateless JWT authentication with cryptographic signature validation. Direct permission validation on every authenticated request. | **PASS** |
| **A08: Software and Data Integrity Failures** | Strict schema validation, duplicate detection in-memory and against database, atomic rollback in Strict Mode. | **PASS** |
| **A09: Security Logging and Monitoring Failures** | Non-repudiation audit logging in `data_import_audit_logs`. IP address and authenticated user captured on all actions. | **PASS** |
| **A10: Server-Side Request Forgery (SSRF)** | Module accepts strictly local multipart streams. No remote URL fetching or external resource resolution occurs. | **PASS** |

---

## 5. Security Recommendations for System Administrators

1. **Rotate Super Admin Credentials Regularly**: Ensure super administrator accounts have high-entropy passwords.
2. **Review Direct Permissions Monthly**: Periodically audit the `user_permissions` table or UI Permission Control tab to revoke import rights when personnel roles change.
3. **Maintain Backup Before Major Annual Migrations**: While the import engine enforces transactional rollbacks, always retain an active database snapshot before onboarding new academic sessions.
