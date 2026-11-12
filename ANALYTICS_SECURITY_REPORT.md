# Bluebells Management Analytics — Security & Data Protection Report

**Module**: Management Analytics  
**Focus**: Least Privilege Access Control & Sensitive Data Protection  
**Auditor**: Ahmed Ali Khan <medlykhan@gmail.com> (Application Security Engineer)  
**Date**: November 2026  

---

## 1. Security Context & Threat Vectors

School management analytics aggregate sensitive data from across the institution, including:
1. **Financial Records**: Fee collections, fee arrears, fee concession beneficiaries.
2. **Student Vulnerabilities**: Chronic absenteeism lists, students requiring academic support/remediation.
3. **Faculty Metrics**: Teacher allocations, workload hours, staff attendance rates.

Unrestricted or broad access to analytics creates significant privacy and compliance liabilities under educational data privacy standards.

---

## 2. Security Controls & Authorization Matrix

| Endpoint | Target Information | Permitted Roles | Unauthorized Roles & Response |
| :--- | :--- | :--- | :--- |
| `GET /api/v1/analytics/overview` | Full executive multi-domain dashboard | `SUPER_ADMIN`, `SCHOOL_ADMIN`, `PRINCIPAL`, `VICE_PRINCIPAL` | `ROLE_ACCOUNTANT`, `ROLE_TEACHER`, `ROLE_PARENT`, `ROLE_STUDENT` -> **HTTP 403 Forbidden** |
| `GET /api/v1/analytics/students` | Demographic distributions & growth | `SUPER_ADMIN`, `SCHOOL_ADMIN`, `PRINCIPAL`, `VICE_PRINCIPAL` | `ROLE_ACCOUNTANT`, `ROLE_TEACHER`, `ROLE_PARENT`, `ROLE_STUDENT` -> **HTTP 403 Forbidden** |
| `GET /api/v1/analytics/attendance` | Absenteeism trends & chronic absentees | `SUPER_ADMIN`, `SCHOOL_ADMIN`, `PRINCIPAL`, `VICE_PRINCIPAL` | `ROLE_ACCOUNTANT`, `ROLE_TEACHER`, `ROLE_PARENT`, `ROLE_STUDENT` -> **HTTP 403 Forbidden** |
| `GET /api/v1/analytics/academics` | Exam averages & support alerts | `SUPER_ADMIN`, `SCHOOL_ADMIN`, `PRINCIPAL`, `VICE_PRINCIPAL` | `ROLE_ACCOUNTANT`, `ROLE_TEACHER`, `ROLE_PARENT`, `ROLE_STUDENT` -> **HTTP 403 Forbidden** |
| `GET /api/v1/analytics/financial` | Fee billing, collection rates & ledger | `SUPER_ADMIN`, `SCHOOL_ADMIN`, `PRINCIPAL`, `VICE_PRINCIPAL`, `ROLE_ACCOUNTANT` | `ROLE_TEACHER`, `ROLE_PARENT`, `ROLE_STUDENT` -> **HTTP 403 Forbidden** |
| `GET /api/v1/analytics/lms` | Courseware engagement & completions | `SUPER_ADMIN`, `SCHOOL_ADMIN`, `PRINCIPAL`, `VICE_PRINCIPAL` | `ROLE_ACCOUNTANT`, `ROLE_PARENT`, `ROLE_STUDENT` -> **HTTP 403 Forbidden** |
| `GET /api/v1/analytics/staff` | Faculty headcount & workload | `SUPER_ADMIN`, `SCHOOL_ADMIN`, `PRINCIPAL`, `VICE_PRINCIPAL` | `ROLE_ACCOUNTANT`, `ROLE_PARENT`, `ROLE_STUDENT` -> **HTTP 403 Forbidden** |
| `GET /api/v1/analytics/export/excel` | Multi-sheet Excel workbook | `SUPER_ADMIN`, `SCHOOL_ADMIN`, `PRINCIPAL`, `VICE_PRINCIPAL`, `ROLE_ACCOUNTANT` | `ROLE_PARENT`, `ROLE_STUDENT` -> **HTTP 403 Forbidden** (Accountant strictly receives sanitized 2-sheet finance export) |

---

## 3. Automated Security Verification

The security posture is enforced in automated integration tests:
- `ManagementAnalyticsTest.testStudentForbiddenFromAnalytics`: Asserts `HTTP 403 Forbidden` when invoked with student bearer token.
- `ManagementAnalyticsTest.testAccountantForbiddenFromFullOverview`: Asserts `HTTP 403 Forbidden` when invoked with accountant bearer token.
- `ManagementAnalyticsTest.testAccountantCanAccessFinancialAnalytics`: Asserts `HTTP 200 OK` on `/financial`.
- `ManagementAnalyticsTest.testAccountantCanExportFinancialExcel`: Asserts generation of a valid finance workbook without demographic or faculty data.

---

## 4. Security Verdict

The Analytics module satisfies the Principle of Least Privilege. Data boundaries are enforced at both controller and service layers.
