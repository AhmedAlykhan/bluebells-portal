# Bluebells Schooling System — Complete API Reference & Endpoint Catalog

**Base URL**: `/api/v1`  
**Authentication**: Header `Authorization: Bearer <JWT_TOKEN>`  
**Response Format**: `ApiResponse<T>` (`success`, `message`, `data`, `timestamp`)  
**Error Format**: Standardized JSON with error code, message, and timestamp  
**Date**: November 2026  

---

## 1. Authentication & Session APIs (`/api/v1/auth`)

| Method | Endpoint | Description | Permitted Roles |
| :--- | :--- | :--- | :--- |
| `POST` | `/auth/login` | Authenticate user with username/email and password | Public |
| `GET` | `/auth/me` | Fetch currently authenticated user profile & linked students | Authenticated |
| `POST` | `/auth/switch-persona` | Switch between authorized user personas | Super Admin |
| `POST` | `/auth/logout` | Invalidate current user session | Authenticated |

---

## 2. Admissions & Student Directory APIs (`/api/v1/admissions`, `/api/v1/students`)

| Method | Endpoint | Description | Permitted Roles |
| :--- | :--- | :--- | :--- |
| `GET` | `/admissions` | List admission applications with status filter | Admin, Principal, Registrar |
| `POST` | `/admissions` | Submit new student admission inquiry | Public / Admin |
| `PATCH` | `/admissions/{id}/status` | Update admission status (`SUBMITTED`, `VERIFIED`, `ACCEPTED`) | Admin, Principal |
| `POST` | `/admissions/{id}/enroll` | Formally enroll applicant into class & section | Admin, Principal |
| `GET` | `/students` | Search and filter student directory | Admin, Teachers, Accountant |
| `GET` | `/students/{id}` | Fetch student detailed dossier | Admin, Teacher, Parent (own child), Student (self) |

---

## 3. Attendance Management APIs (`/api/v1/attendance`)

| Method | Endpoint | Description | Permitted Roles |
| :--- | :--- | :--- | :--- |
| `POST` | `/attendance/record` | Record batch daily attendance for a class section | Teachers, Admin |
| `GET` | `/attendance/student/{id}` | Get monthly attendance history for a student | Admin, Teacher, Parent (own child), Student (self) |
| `GET` | `/attendance/class-summary` | Class-wise daily attendance report | Admin, Principal |

---

## 4. Fee & Financial Management APIs (`/api/v1/finance`, `/api/v1/fees`)

| Method | Endpoint | Description | Permitted Roles |
| :--- | :--- | :--- | :--- |
| `GET` | `/finance/invoices` | List invoices with status & class filters | Admin, Accountant |
| `POST` | `/finance/invoices/generate-monthly` | Generate monthly fee vouchers for active students | Admin, Accountant |
| `GET` | `/finance/invoices/{id}` | Get detailed invoice information | Admin, Accountant, Parent (own child), Student (self) |
| `POST` | `/finance/invoices/{id}/payments` | Record fee payment with receipt | Admin, Accountant |
| `GET` | `/finance/invoices/{id}/challan-pdf` | Download official 3-copy bank challan PDF | Admin, Accountant, Parent (own child), Student (self) |

---

## 5. Examinations & Report Cards APIs (`/api/v1/examinations`, `/api/v1/report-cards`)

| Method | Endpoint | Description | Permitted Roles |
| :--- | :--- | :--- | :--- |
| `GET` | `/examinations` | List scheduled academic examinations | Admin, Teachers, Parents, Students |
| `POST` | `/examinations/{id}/marks` | Enter or update subject marks | Subject Teachers, Admin |
| `POST` | `/examinations/{id}/publish` | Finalize and publish exam results | Principal, Super Admin |
| `GET` | `/report-cards/student/{id}` | Fetch student official report cards | Admin, Teacher, Parent (own child), Student (self) |
| `GET` | `/report-cards/{id}/pdf` | Generate & download Sindh Board A3 Report Card PDF | Admin, Teacher, Parent (own child), Student (self) |

---

## 6. Management Analytics APIs (`/api/v1/analytics`)

| Method | Endpoint | Description | Permitted Roles |
| :--- | :--- | :--- | :--- |
| `GET` | `/analytics/overview` | Full executive multi-domain analytics | Leadership (Super Admin, Admin, Principal, Vice Principal) |
| `GET` | `/analytics/students` | Student demographic distribution & growth | Leadership |
| `GET` | `/analytics/attendance` | Attendance rates & chronic absentees list | Leadership |
| `GET` | `/analytics/academics` | Exam averages & academic support alerts | Leadership |
| `GET` | `/analytics/financial` | Fee billing, collection rates & revenue ledger | Leadership, Accountant |
| `GET` | `/analytics/lms` | LMS courseware views & assignment rates | Leadership |
| `GET` | `/analytics/staff` | Faculty headcount & workload distribution | Leadership |
| `GET` | `/analytics/export/excel` | Download multi-sheet Excel report (Leadership: 6 sheets, Accountant: 2 sheets) | Leadership, Accountant |

---

## 7. Student Development & Awards APIs (`/api/v1/development`)

| Method | Endpoint | Description | Permitted Roles |
| :--- | :--- | :--- | :--- |
| `GET` | `/development/students/{studentId}` | Get student holistic development portfolio | Admin, Teacher, Parent (own child), Student (self) |
| `POST` | `/development/records` | Submit achievement or co-curricular award | Teachers (pending review), Leadership (verified) |
| `PATCH` | `/development/records/{id}/verify` | Leadership verification of student achievement | Leadership (Super Admin, Admin, Principal, Vice Principal) |
| `PATCH` | `/development/records/{id}/reject` | Reject achievement submission with reason | Leadership |
| `GET` | `/development/records/{id}/evidence` | Authenticated evidence document/photo download | Admin, Teacher, Parent (own child), Student (self) |

---

## 8. Digital Student ID Card APIs (`/api/v1/id-cards`)

| Method | Endpoint | Description | Permitted Roles |
| :--- | :--- | :--- | :--- |
| `GET` | `/id-cards/student/{studentId}` | Get active student ID card | Admin, Teacher, Parent (own child), Student (self) |
| `POST` | `/id-cards/student/{studentId}/generate` | Generate official smart student ID badge | Admin, Principal |
| `POST` | `/id-cards/{id}/reissue` | Reissue card with incremented reissue count | Admin, Principal |
| `GET` | `/id-cards/verify-token/{token}` | Public cryptographic QR token verification | Public (Rate Limited) |

---

## 9. Official Certificates APIs (`/api/v1/certificates`)

| Method | Endpoint | Description | Permitted Roles |
| :--- | :--- | :--- | :--- |
| `GET` | `/certificates` | List issued certificates registry | Admin, Principal |
| `POST` | `/certificates/issue` | Issue new tamper-resistant certificate | Admin, Principal |
| `POST` | `/certificates/{id}/revoke` | Revoke certificate with mandatory reason | Admin, Principal |
| `GET` | `/certificates/verify/{code}` | Public online certificate verification | Public (Rate Limited) |
| `GET` | `/certificates/{id}/pdf` | Generate high-resolution print PDF | Admin, Principal, Parent (own child), Student (self) |

---

## 10. Support & Help Desk Centre APIs (`/api/v1/support/tickets`)

| Method | Endpoint | Description | Permitted Roles |
| :--- | :--- | :--- | :--- |
| `GET` | `/support/tickets` | List tickets (Parents: own only; Staff: campus queue) | All Authenticated Personas |
| `POST` | `/support/tickets` | Submit new inquiry ticket | All Authenticated Personas |
| `GET` | `/support/tickets/{id}` | Get ticket details and conversation thread | Creator, Staff, Leadership |
| `POST` | `/support/tickets/{id}/messages` | Post reply (Staff can post internal notes) | Creator, Staff |
| `PATCH` | `/support/tickets/{id}/status` | Update status (`IN_PROGRESS`, `RESOLVED`, `CLOSED`) | Staff, Leadership |
| `POST` | `/support/tickets/{id}/escalate` | Escalate ticket to Executive School Leadership | Staff, Leadership |

---

## 11. Integrations Console APIs (`/api/v1/integrations`)

| Method | Endpoint | Description | Permitted Roles |
| :--- | :--- | :--- | :--- |
| `GET` | `/integrations` | List all connectors with masked credentials | Super Admin, School Admin, Principal |
| `PUT` | `/integrations/{key}` | Update gateway credentials or active status | Super Admin, School Admin |
| `POST` | `/integrations/{key}/test` | Trigger gateway test and measure latency | Super Admin, School Admin, Principal |
| `POST` | `/integrations/{key}/ping` | Heartbeat health ping endpoint | Super Admin, School Admin, Principal |

---

## 12. School Virtual Assistant API (`/api/v1/assistant`)

| Method | Endpoint | Description | Permitted Roles |
| :--- | :--- | :--- | :--- |
| `POST` | `/assistant/query` | Ask factual question to Bluebells Virtual Assistant | All Authenticated Personas |
