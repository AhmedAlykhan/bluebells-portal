# Bluebells Schooling System – Import Template Guide & Data Dictionary
## Official Data Preparation & Formatting Reference for School Administrators

---

## 1. Introduction & Overview

This guide provides school administrators, registrars, and IT staff at **Bluebells Schooling System** with exact specifications for preparing student and staff records for bulk data import.

The system supports two standardized formats:
1. **Microsoft Excel (`.xlsx`)**: Recommended. Includes styled headers, frozen panes, automatic in-cell dropdown validations, and an integrated **Reference Data** tab.
2. **Comma-Separated Values (`.csv`)**: Lightweight plain-text format for programmatic exports and legacy systems. Must follow standard UTF-8 encoding.

---

## 2. Student Import Template Specification

### 2.1 Student Data Fields & Constraints

| Column Header | Field Name | Required? | Data Type | Example / Format | Constraints & Validation Rules |
|---|---|---|---|---|---|
| `admission_number` | Admission Number | **Required** | String | `BSS-2026-001` | Unique across the entire school. Max 50 chars. Recommended format: `BSS-YYYY-XXX`. |
| `first_name` | First Name | **Required** | String | `Muhammad` | Max 100 chars. Formula prefixes (`=`, `+`, `-`, `@`) will be sanitized. |
| `last_name` | Last Name | **Required** | String | `Ali` | Max 100 chars. |
| `gender` | Gender | **Required** | Enum | `MALE` | Must be `MALE`, `FEMALE`, or `OTHER`. (Selectable via Excel dropdown). |
| `date_of_birth` | Date of Birth | **Required** | Date | `2012-05-14` | Format: `YYYY-MM-DD`. Must be in the past. |
| `class_name` | Class / Grade | **Required** | String | `Grade 10` | Must match an active Class in Bluebells DB. Sourced from Reference tab. |
| `section_name` | Section | **Required** | String | `A` | Must match an existing Section for the specified Class (e.g. `A`, `B`, `C`). |
| `academic_session` | Academic Session | Optional | String | `2025-2026` | Defaults to active session if left blank. Sourced from Reference tab. |
| `roll_number` | Roll Number | Optional | String | `10-A-01` | Unique within the class section. |
| `guardian_name` | Guardian Name | **Required** | String | `Tariq Mehmood` | Full name of primary parent / guardian. |
| `guardian_relationship` | Relationship | **Required** | Enum | `FATHER` | Must be `FATHER`, `MOTHER`, `GUARDIAN`, or `OTHER`. |
| `guardian_phone` | Guardian Phone | **Required** | String | `0300-1234567` | Pakistani mobile: `03XX-XXXXXXX` or `+923XXXXXXXXX`. Used for deduplication. |
| `guardian_email` | Guardian Email | Optional | Email | `tariq.mehmood@example.com` | Valid RFC 5322 email syntax. |
| `guardian_cnic` | Guardian CNIC | Optional | String | `41302-1234567-1` | 13-digit Pakistani National ID: `XXXXX-XXXXXXX-X`. |
| `address` | Residential Address | Optional | String | `House 42, Block C, Latifabad Unit 7, Hyderabad` | Max 500 chars. |
| `city` | City | Optional | String | `Hyderabad` | Defaults to `Hyderabad` if omitted. |
| `blood_group` | Blood Group | Optional | Enum | `B+` | `A+`, `A-`, `B+`, `B-`, `AB+`, `AB-`, `O+`, `O-`. |
| `b_form_number` | B-Form Number | Optional | String | `41302-9876543-1` | Student Child Registration Certificate (CRC) / B-Form. |
| `fee_category` | Fee Category | Optional | String | `STANDARD` | Sourced from Reference tab. Options: `STANDARD`, `SCHOLARSHIP_50`, `SIBLING_DISCOUNT`. |
| `emergency_contact` | Emergency Contact | Optional | String | `0333-7654321` | Alternate phone number for medical emergencies. |

---

## 3. Staff Import Template Specification

### 3.1 Staff Data Fields & Constraints

| Column Header | Field Name | Required? | Data Type | Example / Format | Constraints & Validation Rules |
|---|---|---|---|---|---|
| `employee_code` | Employee Code | **Required** | String | `EMP-101` | Unique staff identifier. Max 50 chars. |
| `first_name` | First Name | **Required** | String | `Ahmed` | Max 100 chars. |
| `last_name` | Last Name | **Required** | String | `Khan` | Max 100 chars. |
| `email` | Institutional Email | **Required** | Email | `ahmed.khan@bluebells.edu.pk` | Unique system login username. Valid email format. |
| `phone_number` | Mobile Phone | **Required** | String | `0312-3456789` | Primary contact number: `03XX-XXXXXXX` or `+923XXXXXXXXX`. |
| `gender` | Gender | **Required** | Enum | `MALE` | `MALE`, `FEMALE`, or `OTHER`. |
| `cnic` | National CNIC | **Required** | String | `41303-7654321-3` | 13 digits with hyphens: `XXXXX-XXXXXXX-X`. Unique across staff. |
| `designation` | Designation | **Required** | String | `Senior Mathematics Teacher` | Official institutional title. |
| `department` | Department | **Required** | String | `Science & Mathematics` | Academic or administrative unit. |
| `role_name` | System Portal Role | **Required** | Enum | `ROLE_TEACHER` | Must match: `ROLE_TEACHER`, `ROLE_ACCOUNTANT`, `ROLE_ADMIN`, `ROLE_PRINCIPAL`, `ROLE_VICE_PRINCIPAL`. |
| `qualification` | Highest Qualification | Optional | String | `M.Sc. Mathematics, University of Sindh` | Max 255 chars. |
| `joining_date` | Date of Joining | Optional | Date | `2024-08-01` | Format: `YYYY-MM-DD`. Defaults to current date if blank. |
| `assigned_class` | Primary Assigned Class | Optional | String | `Grade 10` | Used to create initial `TeacherAssignment`. Sourced from Reference tab. |
| `assigned_section` | Primary Section | Optional | String | `A` | Sourced from Reference tab. |
| `assigned_subject` | Teaching Subject | Optional | String | `Mathematics` | Sourced from Reference tab. |
| `basic_salary` | Basic Salary (PKR) | Optional | Number | `75000` | Numeric value in Pakistani Rupees. |
| `emergency_contact` | Emergency Contact Phone | Optional | String | `0301-9988776` | Alternate phone number. |

---

## 4. Understanding the Reference Data Sheets

The generated `.xlsx` templates contain two worksheets:
1. **Data Sheet** (`Students` or `Staff`): Where data is entered.
2. **Reference Data Sheet**: A protected reference table populated with live values from your school database:
   - **Classes & Sections**: Active combinations (e.g. `Playgroup`, `Nursery`, `Kindergarten`, `Grade 1` through `Grade 10`).
   - **Academic Sessions**: Valid sessions (e.g. `2024-2025`, `2025-2026`).
   - **Fee Categories**: Surcharge/concession profiles (e.g. `STANDARD`, `STAFF_CHILD_CONCESSION`, `MERIT_SCHOLARSHIP`).
   - **Faculty Roles**: System roles (`ROLE_TEACHER`, `ROLE_ACCOUNTANT`, `ROLE_PRINCIPAL`, `ROLE_VICE_PRINCIPAL`).
   - **Subjects**: Registered syllabus subjects (`Mathematics`, `Physics`, `Chemistry`, `English`, `Urdu`, `Sindhi`, `Islamiat`, `Computer Science`).

> [!TIP]
> Always download a fresh template before a large import cycle to ensure dropdown lists reflect newly created classes or subjects.

---

## 5. Sample Realistic Data Rows

### 5.1 Sample Student Rows (Latifabad Campus)

```csv
admission_number,first_name,last_name,gender,date_of_birth,class_name,section_name,academic_session,roll_number,guardian_name,guardian_relationship,guardian_phone,guardian_email,guardian_cnic,address,city,blood_group,b_form_number,fee_category,emergency_contact
BSS-2026-001,Ali,Khan,MALE,2011-03-15,Grade 10,A,2025-2026,10-A-01,Tariq Mehmood Khan,FATHER,0300-3012345,tariq.khan@gmail.com,41302-1234567-1,B-14 Auto Bhan Road Unit 3 Latifabad,Hyderabad,B+,41302-9988771-1,STANDARD,0333-2612345
BSS-2026-002,Fatima,Zehra,FEMALE,2012-07-22,Grade 9,B,2025-2026,09-B-04,Syed Asad Ali,FATHER,0321-2233445,asad.ali@yahoo.com,41303-9876543-2,Plot 105 Defence Colony Cantt,Hyderabad,A+,41303-1122334-2,STANDARD,0315-7766554
BSS-2026-003,Bilal,Ahmed,MALE,2013-11-09,Grade 8,A,2025-2026,08-A-12,Rashid Ahmed,FATHER,0345-3344556,rashid.ahmed@outlook.com,41304-5544332-1,Street 5 Citizen Colony Qasimabad,Hyderabad,O+,41304-8899001-1,SIBLING_DISCOUNT,0302-8877665
BSS-2026-004,Ayesha,Siddiqua,FEMALE,2014-04-18,Grade 7,C,2025-2026,07-C-08,Muhammad Siddique,FATHER,0301-4455667,siddique@business.pk,41302-7788990-4,Flat 402 Gul Center Saddar,Hyderabad,AB+,41302-3344556-2,STANDARD,0334-9988112
BSS-2026-005,Hamza,Memon,MALE,2015-09-30,Grade 6,A,2025-2026,06-A-03,Abdul Karim Memon,FATHER,0333-5566778,ak.memon@gmail.com,41301-1239874-5,Unit 6 Near Chandni Cinema Latifabad,Hyderabad,B-,41301-5566778-3,STANDARD,0322-1144778
```

### 5.2 Sample Staff Rows (Bluebells Faculty)

```csv
employee_code,first_name,last_name,email,phone_number,gender,cnic,designation,department,role_name,qualification,joining_date,assigned_class,assigned_section,assigned_subject,basic_salary,emergency_contact
EMP-101,Ahmed,Khan,ahmed.khan@bluebells.edu.pk,0312-3456789,MALE,41303-7654321-3,Senior Mathematics Teacher,Science & Mathematics,ROLE_TEACHER,M.Sc. Mathematics University of Sindh,2023-01-15,Grade 10,A,Mathematics,85000,0300-9876543
EMP-102,Zainab,Rashid,zainab.rashid@bluebells.edu.pk,0322-9876543,FEMALE,41302-8877665-2,Physics Lecturer,Science & Mathematics,ROLE_TEACHER,M.Sc. Applied Physics NED UET,2023-08-01,Grade 10,A,Physics,80000,0333-1122334
EMP-103,Farhan,Qureshi,farhan.qureshi@bluebells.edu.pk,0334-1122334,MALE,41304-9988776-1,Campus Accountant,Finance & Accounts,ROLE_ACCOUNTANT,MBA Finance University of Karachi,2022-04-10,,,70000,0314-5566778
EMP-104,Nadia,Bibi,nadia.bibi@bluebells.edu.pk,0345-2233445,FEMALE,41302-3344551-4,Primary Class In-charge,Primary Wing,ROLE_TEACHER,B.Ed. University of Sindh,2024-02-01,Grade 3,A,General Science,60000,0301-8899001
EMP-105,Tariq,Jameel,tariq.jameel@bluebells.edu.pk,0300-8877112,MALE,41301-4455667-5,Urdu & Sindhi Faculty,Languages & Literature,ROLE_TEACHER,M.A. Urdu & Sindhi Literature,2021-09-01,Grade 9,A,Urdu,75000,0321-9988223
```

---

## 6. Common Mistakes & How to Resolve Them

### Mistake 1: Copy-pasting formulas from other spreadsheets
- **Symptom**: Values like `=SUM(A1:B1)` or `=VLOOKUP(...)` trigger formula sanitization warnings.
- **Fix**: In Excel, copy your source data and use **Paste Special → Values Only** (`Ctrl+Alt+V` or `Cmd+Shift+V`) into the Bluebells template.

### Mistake 2: Missing dashes in CNIC
- **Symptom**: Error: `Invalid CNIC format. Expected XXXXX-XXXXXXX-X`.
- **Fix**: Pakistani CNIC must have 13 digits separated by two hyphens. Example: `41302-1234567-1`.

### Mistake 3: Invalid Phone Number format
- **Symptom**: Error: `Invalid phone format`.
- **Fix**: Ensure numbers begin with a valid mobile code: `0300`, `0301`, `0312`, `0321`, `0333`, `0345`, etc. Hyphens are optional but recommended: `0300-1234567`.

### Mistake 4: Non-existent Class or Section name
- **Symptom**: Error: `Class 'Grade 11' does not exist in academic records`.
- **Fix**: Consult the second tab **Reference Data** in the Excel template. Classes must match live DB names exactly (e.g. `Grade 10`, `Kindergarten`).

### Mistake 5: Duplicate Admission Numbers
- **Symptom**: Marked as `DUPLICATE` in the validation table.
- **Fix**: Each student requires a distinct admission number. In **Import Valid Only** mode, duplicates are safely skipped. In **Strict Mode**, all duplicates must be updated before the batch can be committed.
