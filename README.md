# Bluebells School LMS & School Management Portal

[![Java 17](https://img.shields.io/badge/Java-17%20LTS-blue.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.3-blue.svg)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.5-blue.svg)](https://www.typescriptlang.org/)
[![Vite](https://img.shields.io/badge/Vite-5.4-purple.svg)](https://vitejs.dev/)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind-3.4-38B2AC.svg)](https://tailwindcss.com/)
[![MySQL](https://img.shields.io/badge/MySQL-8.x-orange.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-Proprietary-red.svg)]()

> **Production-grade Learning Management System (LMS) and School Management Portal** engineered specifically for **Bluebells Schooling System** in Hyderabad, Sindh, Pakistan.
> Supporting Administrators, Principals, Teachers, Students, Parents, and Accountants with strict Role-Based Access Control (RBAC) and row-level data isolation.

---

## 🏫 School Profile

- **Institution**: Bluebells Schooling System
- **Campus**: Adjacent Autobahn Road, Next to VIP Garden, Unit 2, Latifabad, Hyderabad, Sindh, Pakistan (Postal Code: `71800`)
- **Contact**: `Bluebells.schoolingsystem@gmail.com` | `+92 316 9681932`
- **Default Currency**: Pakistani Rupee (`PKR`)
- **Default Time Zone**: `Asia/Karachi` (UTC+5)

---

## 🚀 3-Minute Quickstart (Easy Onboarding)

### 1. Prerequisites
Ensure you have the following installed locally:
- **Java 17 LTS**: `java -version`
- **Node.js 18+ & npm**: `node -v` && `npm -v`
- **MySQL 8.x**: Running locally on port `3306`

### 2. Setup MySQL Databases
Create the local development and test databases:
```bash
mysql -h 127.0.0.1 -P 3306 -u root -e "
  CREATE DATABASE IF NOT EXISTS bluebells_school_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
  CREATE DATABASE IF NOT EXISTS bluebells_school_test_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
"
```

*(Optional)* Seed realistic demo classes, faculty, students, grades, fee structures, and timetable:
```bash
mysql -h 127.0.0.1 -P 3306 -u root bluebells_school_db < scripts/seed-demo-data.sql
```

### 3. Launch Stack with One Command
Execute the all-in-one local startup script:
```bash
./scripts/start-local.sh
```

This automatically:
1. Verifies MySQL connectivity.
2. Builds and launches the Spring Boot backend on **port 8080**.
3. Launches the Vite React frontend on **port 5173**.
4. Writes PID files and redirects logs to `backend.log` and `frontend.log`.

### 4. Stop the Local Stack
```bash
./scripts/stop-local.sh
```

---

## 🔑 Demo User Accounts

All demonstration user accounts are pre-configured with the standard password:
> **Default Password for All Demo Accounts**: `Admin@12345`

| Role | Username | Full Name / Persona | Accessible Scope |
| :--- | :--- | :--- | :--- |
| **Super Admin** | `admin` | Platform Administrator | Full system configuration, security audit, all modules |
| **Principal** | `principal` | Dr. Asim Farooqui | Academics, staff, exams, admissions, report approval |
| **Teacher** | `teacher.ahmed` | Sir Ahmed Khan (Math) | Assigned classes (9-A, 10-A), coursework, attendance |
| **Teacher** | `teacher.fatima` | Miss Fatima Shah (Physics) | Assigned classes (9-A), coursework, submissions grading |
| **Accountant** | `accountant` | Syed Tariq Mahmood | Invoices, fee collection, discount policies, financial exports |
| **Parent** | `parent.khan` | Mohammad Tariq Khan | Isolated portal: Children (Ali & Sara Khan), fees, grades |
| **Student** | `student.ali` | Ali Khan (Class 9-A) | Personalized portal: LMS coursework, timetable, marks |
| **Student** | `student.sara` | Sara Khan (Class 8-A) | Personalized portal: LMS coursework, timetable, marks |

---

## 🌐 Service Endpoints

| Service | URL | Description |
| :--- | :--- | :--- |
| **Frontend Portal** | [http://localhost:5173](http://localhost:5173) | Single-page application UI |
| **REST API Base** | [http://localhost:8080/api/v1](http://localhost:8080/api/v1) | Backend REST endpoints |
| **Swagger / OpenAPI** | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) | Interactive API exploration & docs |
| **Actuator Health** | [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health) | System health & readiness status |

---

## 💻 Developer Guide: Running via IDEs

### Running Backend in IntelliJ IDEA
1. Open the project root or the `backend/` folder in IntelliJ IDEA.
2. Ensure **Project SDK** is set to **Java 17** (`File` -> `Project Structure` -> `Project SDK`).
3. Locate `backend/src/main/java/com/bluebells/BluebellsLmsApplication.java`.
4. Right-click and choose **Run 'BluebellsLmsApplication'** or **Debug 'BluebellsLmsApplication'**.
5. Spring Boot will apply Flyway database migrations and start on port `8080`.

### Running Frontend in VS Code
1. Open the `frontend/` folder (or project root) in VS Code.
2. Open terminal (`Ctrl + \`` or `Cmd + \``) and navigate to `frontend`:
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
3. Open [http://localhost:5173](http://localhost:5173) in your browser.

---

## 🧪 Testing & Quality Gates

Run the comprehensive automated test verification:

```bash
# 1. Run all backend tests (38 integration, RBAC, lifecycle, and benchmark tests)
cd backend && mvn test

# 2. Run all frontend Vitest unit & RBAC tests (5 tests)
cd frontend && npm test -- --run

# 3. Typecheck and verify frontend production build
cd frontend && npm run build

# 4. Or execute the complete quality gate drill script:
./scripts/verify-all.sh
```

---

## 📁 Repository Structure

```
portal/
├── backend/                  # Spring Boot 3.3.5 Modular Monolith
│   ├── src/main/java/com/bluebells/
│   │   ├── academics/        # Classes, Sections, Subjects, Academic Sessions, Terms
│   │   ├── admissions/       # Admissions workflow & enrollment
│   │   ├── attendance/       # Daily attendance & audit correction logs
│   │   ├── common/           # Standard API response wrappers & pagination
│   │   ├── examination/      # Exams, grading scales, marks entries, report cards
│   │   ├── exception/        # Global RFC 7807 problem details exception handling
│   │   ├── finance/          # Fee categories, fee structures, invoices, payments
│   │   ├── guardians/        # Parents / Guardians multi-child isolation
│   │   ├── identity/         # Users, roles, permissions, audit logs
│   │   ├── lms/              # Coursework, assignments, student submissions
│   │   ├── notifications/    # Notifications & multi-channel templates
│   │   ├── reports/          # Academic and financial reports & CSV export
│   │   ├── school/           # Institutional profile & settings
│   │   ├── security/         # Spring Security, JWT filters, UserPrincipal, RBAC
│   │   ├── staff/            # Faculty & staff employment records
│   │   ├── students/         # Student profiles & enrollment history
│   │   └── timetable/        # Class schedules & room/teacher conflict detection
│   └── src/main/resources/
│       ├── db/migration/     # Flyway versioned SQL migrations (V1..V16)
│       └── application.yml   # Spring Boot configuration
├── frontend/                 # React 18 + TypeScript + Vite SPA
│   ├── src/
│   │   ├── api/              # Axios client with JWT auto-injection & 401 handling
│   │   ├── components/       # Common UI widgets, layout, navigation
│   │   ├── context/          # AuthContext (JWT management & persona switching)
│   │   ├── pages/            # Role-specific dashboard views & management pages
│   │   ├── types/            # TypeScript data transfer models & schemas
│   │   └── App.tsx           # Route definitions & RBAC route guards
├── docs/                     # Architecture, threat model, user manuals & specs
├── scripts/                  # Shell scripts for backup, restore, startup, & tests
├── README.md                 # Project overview and onboarding guide
└── AGENT.md                  # AI pair programming & automated agent guide
```

---

## 📚 Complete Architectural Documentation

For deep technical details, refer to the accompanying engineering documentation:
- [Agent & AI Engineering Guide](AGENT.md)
- [Architecture & Modular Monolith Design](ARCHITECTURE.md)
- [Database Schema & Entity Specifications](DATABASE_DESIGN.md)
- [Role-Based Access Control (RBAC) Matrix](ROLE_PERMISSION_MATRIX.md)
- [Application Security Policy](SECURITY.md)
- [STRIDE Threat Model](THREAT_MODEL.md)
- [Testing Strategy & Test Plan](TESTING_STRATEGY.md)
- [Release Readiness & Quality Audit Report](RELEASE_READINESS.md)
- [User Manual & Operations Guide](USER_MANUAL.md)
- [Deployment & Operations Guide](DEPLOYMENT_GUIDE.md)
- [Database Backup & Disaster Recovery](BACKUP_AND_RESTORE.md)
- [Engineering Changelog](CHANGELOG.md)
