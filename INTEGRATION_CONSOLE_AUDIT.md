# Bluebells Integrations Console — Architecture & Gateway Security Audit

**Module**: Extensibility & Enterprise Integration Gateways  
**Backend**: `SchoolIntegrationController.java`, `SchoolIntegrationService.java`, `SchoolIntegration.java`  
**Frontend**: `IntegrationsPage.tsx`  
**Auditor**: Ahmed Ali Khan <medlykhan@gmail.com>  
**Date**: November 2026  

---

## 1. Overview & Connector Catalog

The Integrations Console provides a standardized gateway architecture for connecting external school microservices and third-party telecom/banking providers.

### Standard Connectors Supported
1. **SMS Notification Gateway**: Telecom dispatch for absence alerts and fee reminders (Jazz, Telenor, Zong).
2. **WhatsApp Business API**: Direct parent notifications and report card delivery.
3. **Payment & Banking Gateway**: Bank Alfalah and 1Link online voucher payment integration in PKR.
4. **Biometric Gate Attendance**: Turnstile RFID and facial recognition clock-in hardware.
5. **External Calendar Sync**: Google Calendar / iCal school term schedule publishing.

---

## 2. Security Architecture & Remediation

### 2.1. Credential Masking in API Serialization
- **Vulnerability Prior to Audit**: Plaintext storage or direct entity serialization risks leaking merchant passwords or API secret keys to browsers or browser extensions.
- **Remediation**:
  In `SchoolIntegrationDto.java`:
  - `apiKey` is masked (e.g. `jazz_api_99...` or `sk_live_***`).
  - `apiSecret` is permanently masked as `********`.
  - Raw credentials stored encrypted in the database and never exposed in REST payloads.

### 2.2. Dual-Path Heartbeat Monitoring (`/{key}/test` and `/{key}/ping`)
- Frontends and uptime monitors alternate between `/test` and `/ping`.
- `SchoolIntegrationController` maps `@PostMapping({"/{key}/test", "/{key}/ping"})`.
- Connectivity probe executes simulated or live handshake, measures response round-trip latency in milliseconds, records `testTimestamp`, and updates connector health status.

### 2.3. RBAC Enforcement
- Connector configuration and heartbeat testing are restricted to `SUPER_ADMIN`, `SCHOOL_ADMIN`, and `PRINCIPAL`.
- Non-administrative roles (e.g. `ROLE_STUDENT`, `ROLE_PARENT`) receive `HTTP 403 Forbidden`.

---

## 3. Automated Test Verification

All 4 tests in `SchoolIntegrationTest.java` pass:
1. `testGetAllIntegrations`: Confirms connector list retrieval for administrators.
2. `testCredentialsMaskedInDto`: Asserts raw API keys and secrets are never leaked in JSON.
3. `testPingIntegrationHeartbeat`: Asserts dual-path ping/test connectivity check with timestamp.
4. `testStudentForbiddenFromIntegrations`: Asserts student receives HTTP 403 Forbidden.
