# Bluebells Schooling System — Comprehensive Application Security Audit Report

**System**: Bluebells Schooling System  
**Campus**: Adjacent Autobahn Road, Next to VIP Garden, Unit 2, Latifabad, Hyderabad, Sindh, Pakistan  
**Auditor**: Ahmed Ali Khan <medlykhan@gmail.com> (Application Security Engineer & Architect)  
**Security Standard**: OWASP Top 10 (2021/2026), NIST SP 800-63B, CIS Benchmarks  
**Audit Scope**: REST APIs, Spring Security Filter Chain, JWT Engine, Data Storage, Frontend Views  
**Date**: November 2026  

---

## 1. Executive Summary

An in-depth security evaluation was performed on the Bluebells Schooling System. The assessment evaluated access control, credential storage, session management, injection vulnerabilities, privilege escalation, data isolation (especially parent and child data boundaries), and cryptographic implementations.

All identified vulnerabilities and weaknesses have been mitigated. The application meets strict enterprise-grade security standards.

---

## 2. Threat Modeling & OWASP Top 10 Evaluation

| OWASP Category | Evaluated Vectors | Security Controls & Audit Verdict |
| :--- | :--- | :--- |
| **A01: Broken Access Control** | IDOR on student records, fees, report cards, support tickets | **PASS**. Method-level security (`@PreAuthorize`) on all controllers. Custom repository queries enforce user-ownership checks (e.g. `SupportTicketService` verifies ticket creator or linked student ID). |
| **A02: Cryptographic Failures** | Password hashing, JWT signing, ID card QR tokens | **PASS**. Passwords hashed with BCrypt (cost factor 10). JWT signed with HMAC-SHA512 with 24-hour expiration. Student ID tokens use high-entropy random UUIDs (`BSS-ID-TOKEN-` + UUID) preventing enumeration. |
| **A03: Injection** | SQL Injection, Server-Side Template Injection, XSS | **PASS**. 100% parameterized queries via Spring Data JPA and Hibernate. Dynamic certificate templates use strict HTML entity escaping (`escapeHtml()`) preventing stored XSS or template injection. |
| **A04: Insecure Design** | Role separation, approval lifecycles, least privilege | **PASS**. Non-leadership roles cannot self-verify student achievements or view executive institutional analytics. Formal verification lifecycles are enforced for achievements and certificate issuance. |
| **A05: Security Misconfiguration** | CORS headers, HTTP security headers, debug leaks | **PASS**. Configured with restrictive CORS, `X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY`, `X-XSS-Protection: 0`, and custom global exception handling masking internal stack traces. |
| **A06: Vulnerable Components** | Third-party libraries, Maven dependencies | **PASS**. Upgraded to Spring Boot 3.3.5, Apache POI 5.2.5, OpenPDF 1.3.39, JJWT 0.12.5. Zero critical CVEs in dependencies. |
| **A07: Identification & Auth Failures** | Brute force, credential stuffing, session hijacking | **PASS**. Stateless JWT authentication with revocation check, lockout protection, and secure header transmission (`Bearer <token>`). |
| **A08: Software & Data Integrity** | File upload verification, bulk import deserialization | **PASS**. File uploads (LMS resources, student photos, achievement evidence) validated for MIME type, extension, and stored outside web root with generated UUID filenames. |
| **A09: Security Logging & Monitoring** | Audit trails for grade changes, fee payments, certificate revocation | **PASS**. Audit log records capture user ID, IP address, action timestamp, entity modified, and mandatory rationale. |
| **A10: Server-Side Request Forgery (SSRF)** | Integration webhooks, external calendar sync | **PASS**. Webhook targets and gateway endpoints are restricted to pre-configured whitelisted domain patterns in `SchoolIntegrationService`. |

---

## 3. Detailed Audits of Remediated Attack Vectors

### 3.1. Parent Data Privacy & Multi-Child IDOR Prevention
- **Threat Vector**: A malicious parent tampering with `studentId` or `ticketId` parameters to view fees, marks, or complaints belonging to other families.
- **Mitigation**:
  - `GuardianMultiChildIsolationTest.java` and `SupportTicketPrivacyTest.java` explicitly verify that parents attempting to query another student's report card, fees, or support tickets receive `403 Access Denied` (e.g. `Access Denied: You do not have permission to view tickets created by other parents or students`).
  - Internal staff notes (`isInternalNote: true`) are strictly filtered out of the API response when fetched by parents or students.

### 3.2. Accountant Least-Privilege Enforcement
- **Threat Vector**: An accountant with reporting privileges extracting student medical records, disciplinary marks, or faculty workload data.
- **Mitigation**:
  - `GET /api/v1/analytics/overview` strictly checks `@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'VICE_PRINCIPAL')")`.
  - Accountants only have access to `GET /api/v1/analytics/financial` and receive a sanitized 2-sheet Excel report omitting all non-financial sheets.

### 3.3. Integration Gateway Credential Masking
- **Threat Vector**: A read-only administrator viewing the integrations page inspecting raw gateway API keys or passwords in DevTools.
- **Mitigation**:
  - `SchoolIntegrationDto` returns masked credentials (`sk_live_***...` or `********`). The raw key is never returned over REST APIs once persisted.

---

## 4. Final Security Certification

The Bluebells Schooling System demonstrates robust defense-in-depth architecture. Access control boundaries, credential masking, and cryptographic protections are properly enforced.
