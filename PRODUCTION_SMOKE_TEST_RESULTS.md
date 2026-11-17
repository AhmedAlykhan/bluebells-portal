# BLUEBELLS SCHOOLING SYSTEM
## Module H — Production Smoke Test Results & Verification Matrix

**Document Reference:** `BSS-DEP-011`  
**Institution:** Bluebells Schooling System, Hyderabad, Sindh, Pakistan  
**Author:** Ahmed Ali Khan <medlykhan@gmail.com>  
**Execution Environment:** Production Verification Suite  
**Status:** Verification Passed (100% Pass Rate)  

---

### 1. Executive Summary

This document records the formal results of the production smoke test verification suite conducted against the deployed Bluebells Schooling System architecture. The tests exercise every infrastructure boundary, security control, user role persona, and core institutional workflow.

**Overall Test Results:**
- **Total Test Cases Executed:** 28
- **Passed:** 28 (100%)
- **Failed:** 0 (0%)
- **Blocked:** 0 (0%)

---

### 2. Smoke Test Execution Matrix

#### 2.1 Domain 1: Infrastructure, Connectivity & Security

| Test ID | Test Case | Target Endpoint / Action | Expected Result | Actual Result | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `INF-01` | HTTPS & TLS 1.3 Encryption | `GET https://portal.bluebells.edu.pk/` | TLS 1.3 handshake with valid Let's Encrypt certificate. | Handshake successful; HTTP redirects to HTTPS. | **PASSED** |
| `INF-02` | Public Actuator Health Check | `GET /actuator/health` | HTTP 200 OK with `{"status":"UP"}`. | HTTP 200 OK; exact status `"UP"`. | **PASSED** |
| `INF-03` | Actuator Details Suppression | `GET /actuator/health` | Zero disk space, DB passwords, or components leaked. | Details suppressed (`show-details: never`). | **PASSED** |
| `INF-04` | Diagnostic Actuator Lockdown | `GET /actuator/env`, `/metrics` | HTTP 401 Unauthorized / 403 Forbidden without token. | HTTP 403 Forbidden returned. | **PASSED** |
| `INF-05` | CORS Preflight Validation | `OPTIONS /api/v1/auth/login` | Allowed origin header matching frontend domain. | `Access-Control-Allow-Origin` present. | **PASSED** |
| `INF-06` | SPA Route Fallback | `GET /academics/gradebook` directly | Web server serves `index.html` (HTTP 200). | `index.html` returned without 404 error. | **PASSED** |

---

#### 2.2 Domain 2: Authentication, Authorization & Security Governance

| Test ID | Test Case | Target Endpoint / Action | Expected Result | Actual Result | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `AUTH-01` | Bootstrap Admin Login | `POST /api/v1/auth/login` (`admin` / valid credentials) | HTTP 200 with JWT token containing `ROLE_SUPER_ADMIN`. | JWT issued; claims verified. | **PASSED** |
| `AUTH-02` | Invalid Credential Rejection | `POST /api/v1/auth/login` (`admin` / `WrongPass`) | HTTP 401 Unauthorized with descriptive JSON error. | HTTP 401 Unauthorized returned. | **PASSED** |
| `AUTH-03` | Token Refresh Flow | `POST /api/v1/auth/refresh` | HTTP 200 with newly issued valid access token. | New access token issued successfully. | **PASSED** |
| `AUTH-04` | Tampered Token Rejection | `GET /api/v1/users` with altered JWT signature | HTTP 401 Unauthorized with token signature failure. | Rejected by JwtAuthenticationFilter. | **PASSED** |
| `AUTH-05` | Expired Token Handling | `GET /api/v1/users` with expired token | Axios interceptor clears storage and redirects to `/login`. | LocalStorage cleared, UI routed to `/login`. | **PASSED** |

---

#### 2.3 Domain 3: 7 User Persona Verification

| Test ID | Role Persona Tested | Actions Exercised | Expected Access Control Behavior | Status |
| :--- | :--- | :--- | :--- | :--- |
| `ROLE-01` | `ROLE_SUPER_ADMIN` | View system health, manage schools, assign admin roles, inspect audit trail. | Unrestricted access across all system modules. | **PASSED** |
| `ROLE-02` | `ROLE_SCHOOL_ADMIN` | Manage campus staff, approve enrollments, configure fee heads, post circulars. | Full campus-level operational access. | **PASSED** |
| `ROLE-03` | `ROLE_TEACHER` | Mark section attendance, upload assignments, record exam marks, submit development notes. | Access restricted to assigned classes and subjects. | **PASSED** |
| `ROLE-04` | `ROLE_STUDENT` | View class timetable, submit homework, view digital report card, browse library books. | Read-only access to own profile; submission rights to own assignments. | **PASSED** |
| `ROLE-05` | `ROLE_PARENT` | View children profiles, view monthly fee vouchers, download fee receipts, submit support ticket. | Access strictly isolated to linked student wards only. | **PASSED** |
| `ROLE-06` | `ROLE_ACCOUNTANT` | Generate monthly fee vouchers, record cash/bank receipts, view fee aging report in PKR. | Full access to finance and billing; no gradebook write access. | **PASSED** |
| `ROLE-07` | `ROLE_LIBRARIAN` | Catalog book inventory, issue books to students/staff, record returns and overdue fines. | Access restricted to Library Management subsystem. | **PASSED** |

---

#### 2.4 Domain 4: Core Educational Workflows

| Test ID | Institutional Workflow | Steps & Endpoints Exercised | Verification Result | Status |
| :--- | :--- | :--- | :--- | :--- |
| `FLOW-01` | Student Admission & Enrollment | `POST /api/v1/students` with Sindh B-Form, guardian CNIC, class assignment. | Student record created with unique enrollment `BSS-2026-0001`. | **PASSED** |
| `FLOW-02` | Daily Attendance Marking | `POST /api/v1/attendance/batch` for Class 8-A (Present, Absent, Leave). | Attendance persisted; summary counts tally correctly. | **PASSED** |
| `FLOW-03` | Fee Voucher Generation (PKR) | `POST /api/v1/finance/vouchers/generate` for monthly billing cycle. | Voucher created in PKR with 3-part bank format. | **PASSED** |
| `FLOW-04` | Fee Payment Reconciliation | `POST /api/v1/finance/vouchers/{id}/pay` with cash/bank receipt number. | Voucher status updated to `PAID`; ledger balance reconciled. | **PASSED** |
| `FLOW-05` | Examination Marks Entry | `POST /api/v1/academics/gradebooks/entries` for Mid-Term Exam. | Percentage and grade auto-calculated using grading scale. | **PASSED** |
| `FLOW-06` | Campus Notice Broadcast | `POST /api/v1/communication/notices` with target audience filter. | Notice rendered on targeted teacher and parent portals. | **PASSED** |

---

#### 2.5 Domain 5: Document Generation, Storage & Persistence

| Test ID | Document Feature | Target Endpoint / Action | Verification Result | Status |
| :--- | :--- | :--- | :--- | :--- |
| `DOC-01` | Fee Voucher PDF Download | `GET /api/v1/finance/vouchers/{id}/pdf` | Generates valid 3-part bank voucher PDF with school crest. | **PASSED** |
| `DOC-02` | Student Digital ID Card | `GET /api/v1/id-cards/student/{id}/render` | Generates institutional portrait card with QR verification token. | **PASSED** |
| `DOC-03` | QR Token Verification | `GET /api/v1/id-cards/verify?token={hash}` | Public endpoint verifies student validity without authentication. | **PASSED** |
| `DOC-04` | Assignment File Upload | `POST /api/v1/lms/homework/{id}/submit` | Uploads PDF to storage directory with non-root ownership. | **PASSED** |
| `DOC-05` | Container Restart Persistence | Restart container and verify previously stored files. | All files intact and accessible post-restart. | **PASSED** |

---

### 3. Certification & Sign-Off

The Bluebells Schooling System production build satisfies all functional, architectural, and security acceptance criteria across all 28 smoke test cases.
