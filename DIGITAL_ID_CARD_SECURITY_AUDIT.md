# Bluebells Student Digital ID Cards — Security & Cryptographic Token Audit

**Module**: Student Digital Identity & Smart Badges  
**Backend**: `StudentIdCardController.java`, `StudentIdCardService.java`, `StudentIdCard.java`  
**Frontend**: `StudentIdCardsPage.tsx`, `PublicVerifyIdCardPage.tsx`  
**Auditor**: Ahmed Ali Khan <medlykhan@gmail.com>  
**Date**: November 2026  

---

## 1. Overview & Threat Vectors

Smart student identity cards serve as official identification on campus, during examinations, and for bus transit. Each card features:
1. Student photo, roll number, admission number, and class section.
2. Emergency contact and blood group.
3. Official school signature (Academic Principal Huma Arif).
4. Machine-readable barcode and verification QR code.

### Critical Vulnerability Identified Prior to Audit
- **Vulnerability**: QR verification tokens previously embedded predictable admission numbers: `BSS-QR-2026-ADM-0001`.
- **Exploitation Risk**: Attackers could scrape student profiles or forge fake student identity cards simply by guessing sequential admission numbers.

---

## 2. Cryptographic Remediation & Architecture

### 2.1. High-Entropy Random UUID Token Generation
- **Remediation**:
  In `StudentIdCardService.java`:
  ```java
  // Generate a non-guessable, high-entropy cryptographic token
  String token = "BSS-ID-TOKEN-" + UUID.randomUUID().toString();
  card.setQrVerificationToken(token);
  card.setQrPayload(baseUrl + "/verify-id?token=" + token);
  ```
- **Entropy Assessment**: 128-bit UUID v4 entropy eliminates brute-force enumeration attacks.
- **Database Index**: Unique index on `student_id_cards(qr_verification_token)` enables efficient O(1) verification.

### 2.2. Reissue Audit Tracking
- Every reissued card increments `reissue_count` and records the reason (e.g. `LOST`, `DAMAGED`).
- Old tokens are invalidated upon card reissue to prevent duplicate active cards.

### 2.3. Public Verification Endpoint Protection
- The public verification endpoint `GET /api/v1/id-cards/verify-token/{token}` returns strictly verified public identity data (Student Name, Class, Card Status, Expiration Date). Sensitive data such as home addresses or CNIC numbers are omitted.

---

## 3. Automated Test Verification

All 3 tests in `StudentIdCardTest.java` pass:
1. `testGenerateCardWithCryptographicToken`: Asserts card creation with `BSS-ID-TOKEN-` prefix, non-empty payload, and status `ACTIVE`.
2. `testVerifyCardPubliclyViaToken`: Asserts successful token lookup without authentication.
3. `testReissueCard`: Asserts incremented reissue counter and state retention.
