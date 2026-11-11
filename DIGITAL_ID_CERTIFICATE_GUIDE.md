# Bluebells Schooling System — Digital Student ID Cards & Certificates Guide

**Document Reference:** `BSS-IDC-2026-V1`  
**Campus:** Adjacent Autobahn Road, Next to VIP Garden, Unit 2, Latifabad, Hyderabad, Sindh  
**Leadership:** Academic Principal Huma Arif | Vice Principal Hana Khan  
**Classification:** Official Credentialing, Verification & Badge Specifications  

---

## PART 1: DIGITAL & PRINTABLE STUDENT ID CARDS

### 1. Physical & Digital Badge Specifications
All student identity badges adhere to the international **CR80 standard specification** (Dimensions: $85.60\text{ mm} \times 53.98\text{ mm}$ / $3.375\text{ in} \times 2.125\text{ in}$), tailored for dual-sided PVC laminating or digital smartphone wallet rendering.

```
+-------------------------------------------------------------+
| [BSS LOGO] BLUEBELLS SCHOOLING SYSTEM - HYDERABAD           |
|            Excellence in Education - Academic Session 2026  |
|=============================================================|
|                 +---------+                                 |
|                 |  PHOTO  |                                 |
|                 +---------+                                 |
|                      ALI KHAN                               |
|               S/O Mohammad Tariq Khan                       |
|               Grade 8 • Section A                           |
|                                                             |
| Admission No: BSS-2026-0001        Roll No: 12              |
| Blood Group:  B+                   Emergency: +92 316 9681932|
| Valid Thru:   31-05-2027           Card No: IDC-2026-0001   |
|                                                             |
| [Card: IDC-2026-0001]                     Huma Arif         |
|                                       Academic Principal    |
+-------------------------------------------------------------+
```

### 2. Front of Card Elements
1. **Header Banner**: Deep Royal Navy (`#123B70`) background with 4px Royal Gold (`#F59E0B`) accent line, official Bluebells Schooling System insignia, and academic session.
2. **Student Portrait**: Circular avatar framed with a 4px gold border and green active enrollment seal.
3. **Student Legal Name & Father's Name**: Directly resolved from student admissions record.
4. **Academic Class & Section**: Dynamic grade level and section badge.
5. **Key Emergency Indicators**:
   - **Admission Number**: Unique institutional identifier.
   - **Roll Number**: Classroom sequence.
   - **Blood Group**: Critical medical emergency identifier (e.g., $B^+$, $O^+$).
   - **Campus Emergency Helpline**: Official dispatch contact (`+92 316 9681932`).
6. **Executive Signatory**: Digitally stamped signature of **Huma Arif**, Academic Principal.

### 3. Back of Card & Safe QR Code Security Architecture
The reverse side contains school ownership terms, campus coordinates, and a high-contrast **Safe QR Code**.

> [!IMPORTANT]
> **Strict QR Security Protocol**:
> Under no circumstances does the QR code encode sensitive personally identifiable information (PII) such as CNIC/B-Form numbers, student home address, or guardian private mobile numbers.
> 
> The QR code strictly encodes an HTTPS verification payload:
> `https://portal.bluebells.edu.pk/verify-id?card=IDC-2026-0001&sig=BSS-ID-TOKEN-49ca3f03-802c-4cc0-b018-0f966403bb52`

When scanned by campus security guards, transport dispatchers, or authorized personnel, the public verification endpoint (`/api/v1/id-cards/verify/{token}`) validates:
- Whether the badge is authentic and currently `ACTIVE`.
- Student's authorized name, grade, and admission number.
- Expiration date.
- Immediate warning if the badge has been marked `REISSUED` or `EXPIRED`.

### 4. Card Reissue & Replacement Lifecycle
When an ID badge is reported lost or damaged:
1. School administration issues a replacement via `POST /api/v1/id-cards/{id}/reissue` specifying the audit reason (`LOST`, `DAMAGED`, `PROMOTED`, or `DETAILS_UPDATED`).
2. The original card is instantly marked as **`REISSUED`**, invalidating its QR verification token.
3. A replacement badge is generated with an incremented sequence suffix (e.g., `IDC-2026-0001-R1`), a fresh cryptographic token, and an updated audit count.

---

## PART 2: DIGITAL CERTIFICATES & HONORS

### 1. Certificate Categories & Template System
Bluebells Schooling System issues verified digital certificates across six institutional categories:
1. **`MERIT`**: Academic distinction, high percentage achievements, and term toppers.
2. **`ACHIEVEMENT`**: Special commendation in science fairs, robotics, or Olympiads.
3. **`PARTICIPATION`**: Active engagement in school events, seminars, or cultural exhibitions.
4. **`SPORTS`**: Inter-school leagues, annual sports day medals, and athletics cups.
5. **`COMPLETION`**: Grade level completion, secondary school graduation, or specialized workshops.
6. **`CONDUCT`**: Exemplary citizenship, peer leadership, and community service.

### 2. Dynamic Template Replacement Engine
The backend template compiler dynamically substitutes verified database fields into the certificate narrative:
- `{{STUDENT_NAME}}`: Full student recipient name.
- `{{GUARDIAN_NAME}}`: Father or legal guardian name.
- `{{ADMISSION_NUMBER}}`: Student admission ID.
- `{{CLASS}}`: Class / Grade level.
- `{{SECTION}}`: Classroom section.
- `{{SESSION}}`: Academic year (e.g., `2025-2026`).

### 3. Ornate A4 Landscape Printable Format
The certificate layout features:
- **Border Style**: Heavy royal navy and double gold ornate border (`border-8 border-double border-amber-600`).
- **Bluebells Crest**: Official crest rendered at top center.
- **Conferral Calligraphy**: High-contrast serif typography honoring the student.
- **Dual Signatures**:
  - **Huma Arif**, Academic Principal
  - **Hana Khan**, Vice Principal
- **Cryptographic Verification Badge**: Unique verification code (e.g., `VRF-BSS-89C2A1`) allowing employers, universities, and third parties to authenticate the certificate online.

### 4. Public Online Verification Portal
Anyone can verify an issued certificate by navigating to `/certificates` or accessing:
`GET /api/v1/certificates/verify/{code}`

The public endpoint returns:
- Authenticity flag (`authentic: true`)
- Awardee full name and admission number
- Honors title and category
- Class and session of issuance
- Executive signatories
- Status (`ISSUED` vs `REVOKED`)

---

## 5. Security & Verification API Reference

| Endpoint | Method | Auth Required | Description |
| :--- | :--- | :--- | :--- |
| `/api/v1/id-cards/students/{studentId}` | `GET` | Yes (Staff, Parent, Student) | Retrieve active ID card details |
| `/api/v1/id-cards/generate` | `POST` | Yes (Admin, Principal) | Mint new student ID card |
| `/api/v1/id-cards/{id}/reissue` | `POST` | Yes (Admin, Principal) | Reissue lost card with audit trail |
| `/api/v1/id-cards/verify/{token}` | `GET` | **NO (Public)** | Scan QR token to authenticate ID badge |
| `/api/v1/certificates/templates` | `GET` | Yes (Staff) | List active certificate templates |
| `/api/v1/certificates/issue` | `POST` | Yes (Admin, Principal) | Issue certificate with template interpolation |
| `/api/v1/certificates/verify/{code}` | `GET` | **NO (Public)** | Authenticate certificate via verification code |
| `/api/v1/certificates/{id}/revoke` | `POST` | Yes (Admin, Principal) | Revoke certificate with reason |

*Bluebells Schooling System Credential Authority.*
