# Bluebells Official Certificates — Security & Template Integrity Audit

**Module**: Institutional Certificates & Digital Credentialing  
**Backend**: `CertificateController.java`, `CertificateService.java`, `Certificate.java`  
**Frontend**: `CertificatesPage.tsx`  
**Auditor**: Ahmed Ali Khan <medlykhan@gmail.com>  
**Date**: November 2026  

---

## 1. Overview & Threat Vectors

Official school certificates (Leaving Certificates, Character Certificates, Academic Merit, Sports Distinction) are legally significant credentials issued under the authority of Academic Principal Huma Arif and Senior Coordinator Sir Ahmed Khan.

### Identified Vulnerabilities Prior to Audit
1. **Server-Side Template Injection (SSTI) / Stored XSS**: Dynamic placeholders (e.g. `{{STUDENT_NAME}}`, `{{REMARKS}}`) were merged directly into HTML templates without sanitization.
2. **Missing Revocation Rationale**: Certificates could previously be revoked without recording a mandatory explanation, violating audit trail standards.

---

## 2. Security Hardening & Remediation

### 2.1. Template Injection & Stored XSS Defense
- **Remediation**: In `CertificateService.java`, all user-controlled values substituted into certificate HTML templates pass through strict HTML entity escaping:
  ```java
  private String sanitize(String input) {
      if (input == null) return "";
      return org.springframework.web.util.HtmlUtils.htmlEscape(input.trim());
  }
  ```
- Any `<script>`, `<iframe>`, or HTML tags injected via student names or remarks are neutralized into safe text representations (`&lt;script&gt;`).

### 2.2. Mandatory Revocation Reason Enforcement
- In `CertificateService.revokeCertificate`:
  ```java
  if (reason == null || reason.trim().isEmpty()) {
      throw new IllegalArgumentException("A non-blank revocation reason is strictly mandatory for certificate revocation.");
  }
  ```
- Certificates cannot be cancelled silently. The revoking administrator, timestamp, and explanation are permanently recorded.

### 2.3. Public Verification Integrity
- `GET /api/v1/certificates/verify/{code}` verifies authenticity against the database. If revoked, the public verification endpoint explicitly displays:
  - `Status: REVOKED`
  - `Revocation Reason: <Reason>`
  - `Revocation Date: <Date>`

---

## 3. Automated Test Verification

All 3 tests in `CertificateServiceTest.java` pass:
1. `testIssueCertificate`: Asserts template resolution, reference code generation, and active state.
2. `testRevokeRequiresMandatoryReason`: Verifies exception thrown when revocation reason is blank.
3. `testPublicVerification`: Asserts authenticity response containing student name, issue date, and validation status.
