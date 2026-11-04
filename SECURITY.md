# Security Policy & Controls — Bluebells School LMS

## 1. Authentication & Credentials Security

1. **Password Hashing**:
   - Modern adaptive hashing via `BCryptPasswordEncoder` (work factor 12) or Argon2id.
   - Plaintext passwords are never logged, persisted, or held in memory longer than authentication processing.
2. **Password Complexity Policy**:
   - Minimum 8 characters, maximum 64 characters.
   - Requires at least one uppercase letter, one lowercase letter, one digit, and one special character.
3. **Session & Token Management**:
   - Authentication tokens are cryptographically signed using HMAC-SHA256 (256-bit+ secure secret).
   - Short-lived access tokens (15-60 minutes) paired with secure revocation and refresh mechanisms.
   - Tokens are delivered via secure HTTP headers (`Authorization: Bearer <token>`) or `HttpOnly; SameSite=Strict; Secure` cookies.
4. **Brute Force & Abuse Protection**:
   - Per-IP and per-account login throttling: 5 consecutive failed attempts trigger a 15-minute account lock.
   - Centralized security event logging for anomalous login bursts.

---

## 2. Authorization & Least Privilege

1. **Defense in Depth**:
   - Perimeter authorization configured in `SecurityFilterChain`.
   - Method-level security enabled via `@EnableMethodSecurity(prePostEnabled = true)`.
   - Domain-level ownership verification:
     - Teachers verified against `teacher_assignments`.
     - Parents verified against `guardian_student_links`.
     - Students restricted to their own `user.id` and `student.id`.
2. **Deny by Default**:
   - All `/api/v1/**` endpoints require explicit authentication and roles unless explicitly marked in `SecurityConfig` (e.g. `/api/v1/auth/login`, `/api/v1/actuator/health`).

---

## 3. Data Protection & Minor Privacy

1. **Protection of Minor Records**:
   - Student B-Form numbers, emergency contact details, and home addresses are categorized as Confidential Personal Identifiable Information (PII).
   - Unauthenticated endpoints and general directory endpoints omit PII fields.
2. **Spreadsheet Formula Injection Prevention**:
   - All CSV exports sanitize text cells starting with `=`, `+`, `-`, `@`, `\t`, or `\r` by prefixing a single quote `'` to neutralize formula execution in Microsoft Excel or Google Sheets.

---

## 4. Secure File Handling & Document Storage

1. **Strict Content-Type & Extension Allowlist**:
   - Allowed formats: `image/jpeg`, `image/png`, `application/pdf`.
   - Maximum upload limit: 10 MB.
2. **Storage Isolation & Name Sanitization**:
   - Original client filenames are stripped of path characters (`../`, `..\\`) and replaced with cryptographic UUID filenames on disk (`{uuid}.bin`).
   - Files are stored outside the public web root.
3. **Authorized Download Handler**:
   - Downloads are served exclusively through `/api/v1/documents/{id}/download` with server-side ownership verification. Static direct file URL access is disabled.

---

## 5. Security Headers & CORS Policy

- **CORS**: Restricted exclusively to configured frontend origins (e.g. `http://localhost:5173` in development).
- **Security Headers Applied**:
  - `Content-Security-Policy: default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; frame-ancestors 'none';`
  - `X-Content-Type-Options: nosniff`
  - `X-Frame-Options: DENY`
  - `Strict-Transport-Security: max-age=31536000; includeSubDomains` (production)
  - `Referrer-Policy: strict-origin-when-cross-origin`
