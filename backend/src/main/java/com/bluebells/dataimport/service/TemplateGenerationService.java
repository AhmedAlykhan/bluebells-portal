package com.bluebells.dataimport.service;

import com.bluebells.academics.AcademicSession;
import com.bluebells.academics.AcademicSessionRepository;
import com.bluebells.academics.ClassEntity;
import com.bluebells.academics.ClassRepository;
import com.bluebells.academics.Section;
import com.bluebells.academics.SectionRepository;
import com.bluebells.academics.Subject;
import com.bluebells.academics.SubjectRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TemplateGenerationService {

    private static final Logger log = LoggerFactory.getLogger(TemplateGenerationService.class);

    private final AcademicSessionRepository academicSessionRepository;
    private final ClassRepository classRepository;
    private final SectionRepository sectionRepository;
    private final SubjectRepository subjectRepository;

    public TemplateGenerationService(AcademicSessionRepository academicSessionRepository,
                                     ClassRepository classRepository,
                                     SectionRepository sectionRepository,
                                     SubjectRepository subjectRepository) {
        this.academicSessionRepository = academicSessionRepository;
        this.classRepository = classRepository;
        this.sectionRepository = sectionRepository;
        this.subjectRepository = subjectRepository;
    }

    // =========================================================================
    // STUDENT TEMPLATES
    // =========================================================================

    public byte[] generateStudentExcelTemplate() {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            XSSFSheet dataSheet = workbook.createSheet("Students");
            XSSFSheet instructionsSheet = workbook.createSheet("Instructions");
            XSSFSheet refSheet = workbook.createSheet("Reference_Values");

            // Header Style: Navy blue background #1E3A8A, bold white text
            XSSFCellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle textStyle = workbook.createCellStyle();
            DataFormat dataFormat = workbook.createDataFormat();
            textStyle.setDataFormat(dataFormat.getFormat("@"));

            // 1. Build Reference Values Sheet
            populateStudentReferenceSheet(refSheet, headerStyle);

            // 2. Build Instructions Sheet
            populateStudentInstructionsSheet(instructionsSheet, headerStyle);

            // 3. Build Data Sheet Headers
            String[] headers = {
                    "admission_number", "first_name", "last_name", "gender",
                    "date_of_birth", "blood_group", "b_form_number", "residential_address",
                    "admission_date", "academic_session", "class_name", "section_name",
                    "roll_number", "guardian_name", "guardian_relation", "guardian_phone",
                    "guardian_cnic", "guardian_occupation", "guardian_address"
            };

            Row headerRow = dataSheet.createRow(0);
            headerRow.setHeightInPoints(28);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Freeze header row
            dataSheet.createFreezePane(0, 1);

            // Sample rows (fictional data for testing)
            String[][] sampleData = {
                    {"ADM-2024-001", "Bilal", "Ahmed", "MALE", "2012-04-15", "B+", "42101-1234567-1", "House 12, Block 3, Gulshan-e-Iqbal, Karachi", "2024-08-01", "2026-2027", "Class 9", "Section A", "01", "Tariq Ahmed", "FATHER", "+92-300-1122334", "42101-7654321-1", "Civil Engineer", "House 12, Block 3, Gulshan-e-Iqbal, Karachi"},
                    {"ADM-2024-002", "Ayesha", "Khan", "FEMALE", "2012-09-20", "O+", "42101-2345678-2", "Flat 4B, Al-Noor Heights, Clifton, Karachi", "2024-08-01", "2026-2027", "Class 9", "Section A", "02", "Rashid Khan", "FATHER", "+92-321-2233445", "42201-8765432-1", "Chartered Accountant", "Flat 4B, Al-Noor Heights, Clifton, Karachi"},
                    {"ADM-2024-003", "Zainab", "Fatima", "FEMALE", "2013-01-10", "A+", "42101-3456789-4", "Plot 78, Street 5, PECHS Block 2, Karachi", "2024-08-01", "2026-2027", "Class 9", "Section B", "01", "Syed Mansoor", "FATHER", "+92-333-3344556", "42101-9876543-3", "Businessman", "Plot 78, Street 5, PECHS Block 2, Karachi"}
            };

            for (int r = 0; r < sampleData.length; r++) {
                Row row = dataSheet.createRow(r + 1);
                for (int c = 0; c < sampleData[r].length; c++) {
                    Cell cell = row.createCell(c);
                    cell.setCellValue(sampleData[r][c]);
                    cell.setCellStyle(textStyle);
                }
            }

            // Dropdown Validations
            addDropdownValidation(dataSheet, new String[]{"MALE", "FEMALE", "OTHER"}, 3, 1, 1000);
            addDropdownValidation(dataSheet, new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"}, 5, 1, 1000);
            addDropdownValidation(dataSheet, new String[]{"FATHER", "MOTHER", "GUARDIAN"}, 14, 1, 1000);

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                dataSheet.setColumnWidth(i, 20 * 256);
            }
            dataSheet.setColumnWidth(7, 35 * 256); // residential address
            dataSheet.setColumnWidth(18, 35 * 256); // guardian address

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Failed to generate Student Excel template", e);
            throw new RuntimeException("Error generating Student Excel template: " + e.getMessage(), e);
        }
    }

    public byte[] generateStudentCsvTemplate() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
                     "admission_number", "first_name", "last_name", "gender",
                     "date_of_birth", "blood_group", "b_form_number", "residential_address",
                     "admission_date", "academic_session", "class_name", "section_name",
                     "roll_number", "guardian_name", "guardian_relation", "guardian_phone",
                     "guardian_cnic", "guardian_occupation", "guardian_address"
             ))) {

            csvPrinter.printRecord("ADM-2024-001", "Bilal", "Ahmed", "MALE", "2012-04-15", "B+", "42101-1234567-1", "House 12, Block 3, Gulshan-e-Iqbal, Karachi", "2024-08-01", "2026-2027", "Class 9", "Section A", "01", "Tariq Ahmed", "FATHER", "+92-300-1122334", "42101-7654321-1", "Civil Engineer", "House 12, Block 3, Gulshan-e-Iqbal, Karachi");
            csvPrinter.printRecord("ADM-2024-002", "Ayesha", "Khan", "FEMALE", "2012-09-20", "O+", "42101-2345678-2", "Flat 4B, Al-Noor Heights, Clifton, Karachi", "2024-08-01", "2026-2027", "Class 9", "Section A", "02", "Rashid Khan", "FATHER", "+92-321-2233445", "42201-8765432-1", "Chartered Accountant", "Flat 4B, Al-Noor Heights, Clifton, Karachi");
            csvPrinter.printRecord("ADM-2024-003", "Zainab", "Fatima", "FEMALE", "2013-01-10", "A+", "42101-3456789-4", "Plot 78, Street 5, PECHS Block 2, Karachi", "2024-08-01", "2026-2027", "Class 9", "Section B", "01", "Syed Mansoor", "FATHER", "+92-333-3344556", "42101-9876543-3", "Businessman", "Plot 78, Street 5, PECHS Block 2, Karachi");

            csvPrinter.flush();
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Failed to generate Student CSV template", e);
            throw new RuntimeException("Error generating Student CSV template: " + e.getMessage(), e);
        }
    }

    // =========================================================================
    // STAFF TEMPLATES
    // =========================================================================

    public byte[] generateStaffExcelTemplate() {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            XSSFSheet dataSheet = workbook.createSheet("Staff");
            XSSFSheet instructionsSheet = workbook.createSheet("Instructions");
            XSSFSheet refSheet = workbook.createSheet("Reference_Values");

            XSSFCellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle textStyle = workbook.createCellStyle();
            DataFormat dataFormat = workbook.createDataFormat();
            textStyle.setDataFormat(dataFormat.getFormat("@"));

            // 1. Build Reference Values Sheet
            populateStaffReferenceSheet(refSheet, headerStyle);

            // 2. Build Instructions Sheet
            populateStaffInstructionsSheet(instructionsSheet, headerStyle);

            // 3. Build Data Sheet Headers
            String[] headers = {
                    "employee_id", "full_name", "gender", "designation",
                    "department", "category", "qualification", "joining_date",
                    "official_email", "phone_number", "cnic", "basic_salary",
                    "status", "teacher_subject_assignments"
            };

            Row headerRow = dataSheet.createRow(0);
            headerRow.setHeightInPoints(28);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Freeze header row
            dataSheet.createFreezePane(0, 1);

            // Sample rows (fictional staff data)
            String[][] sampleData = {
                    {"STF-2024-001", "Muhammad Rizwan", "MALE", "Senior Teacher", "Mathematics", "TEACHING", "M.Sc Mathematics, B.Ed", "2023-08-01", "rizwan.teacher@bluebells.edu.pk", "+92-300-5544332", "42101-5544332-1", "75000.00", "ACTIVE", "Class 9|Section A|Mathematics;Class 10|Section A|Mathematics"},
                    {"STF-2024-002", "Samina Tariq", "FEMALE", "Secondary Teacher", "English", "TEACHING", "M.A English Literature", "2023-09-15", "samina.tariq@bluebells.edu.pk", "+92-321-6655443", "42201-6655443-2", "65000.00", "ACTIVE", "Class 9|Section A|English Compulsory;Class 9|Section B|English Compulsory"},
                    {"STF-2024-003", "Kashif Niaz", "MALE", "Accounts Officer", "Finance", "ADMINISTRATIVE", "B.Com, ACCA Finalist", "2024-01-10", "kashif.niaz@bluebells.edu.pk", "+92-333-7766554", "42301-7766554-3", "60000.00", "ACTIVE", ""}
            };

            for (int r = 0; r < sampleData.length; r++) {
                Row row = dataSheet.createRow(r + 1);
                for (int c = 0; c < sampleData[r].length; c++) {
                    Cell cell = row.createCell(c);
                    cell.setCellValue(sampleData[r][c]);
                    cell.setCellStyle(textStyle);
                }
            }

            // Dropdown Validations
            addDropdownValidation(dataSheet, new String[]{"MALE", "FEMALE", "OTHER"}, 2, 1, 1000);
            addDropdownValidation(dataSheet, new String[]{"TEACHING", "NON_TEACHING", "ADMINISTRATIVE"}, 5, 1, 1000);
            addDropdownValidation(dataSheet, new String[]{"ACTIVE", "PROBATION", "SUSPENDED"}, 12, 1, 1000);

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                dataSheet.setColumnWidth(i, 22 * 256);
            }
            dataSheet.setColumnWidth(1, 26 * 256); // full name
            dataSheet.setColumnWidth(8, 30 * 256); // email
            dataSheet.setColumnWidth(13, 40 * 256); // assignments

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Failed to generate Staff Excel template", e);
            throw new RuntimeException("Error generating Staff Excel template: " + e.getMessage(), e);
        }
    }

    public byte[] generateStaffCsvTemplate() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
                     "employee_id", "full_name", "gender", "designation",
                     "department", "category", "qualification", "joining_date",
                     "official_email", "phone_number", "cnic", "basic_salary",
                     "status", "teacher_subject_assignments"
             ))) {

            csvPrinter.printRecord("STF-2024-001", "Muhammad Rizwan", "MALE", "Senior Teacher", "Mathematics", "TEACHING", "M.Sc Mathematics, B.Ed", "2023-08-01", "rizwan.teacher@bluebells.edu.pk", "+92-300-5544332", "42101-5544332-1", "75000.00", "ACTIVE", "Class 9|Section A|Mathematics;Class 10|Section A|Mathematics");
            csvPrinter.printRecord("STF-2024-002", "Samina Tariq", "FEMALE", "Secondary Teacher", "English", "TEACHING", "M.A English Literature", "2023-09-15", "samina.tariq@bluebells.edu.pk", "+92-321-6655443", "42201-6655443-2", "65000.00", "ACTIVE", "Class 9|Section A|English Compulsory;Class 9|Section B|English Compulsory");
            csvPrinter.printRecord("STF-2024-003", "Kashif Niaz", "MALE", "Accounts Officer", "Finance", "ADMINISTRATIVE", "B.Com, ACCA Finalist", "2024-01-10", "kashif.niaz@bluebells.edu.pk", "+92-333-7766554", "42301-7766554-3", "60000.00", "ACTIVE", "");

            csvPrinter.flush();
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Failed to generate Staff CSV template", e);
            throw new RuntimeException("Error generating Staff CSV template: " + e.getMessage(), e);
        }
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    private XSSFCellStyle createHeaderStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        // Navy blue color #1E3A8A -> RGB (30, 58, 138)
        byte[] rgb = new byte[]{(byte) 30, (byte) 58, (byte) 138};
        XSSFColor navy = new XSSFColor(rgb, new DefaultIndexedColorMap());
        style.setFillForegroundColor(navy);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);

        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDateStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("yyyy-mm-dd"));
        return style;
    }

    private void addDropdownValidation(XSSFSheet sheet, String[] values, int colIndex, int startRow, int endRow) {
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(values);
        CellRangeAddressList addressList = new CellRangeAddressList(startRow, endRow, colIndex, colIndex);
        DataValidation validation = validationHelper.createValidation(constraint, addressList);
        validation.setShowErrorBox(true);
        validation.setSuppressDropDownArrow(true);
        sheet.addValidationData(validation);
    }

    private void populateStudentReferenceSheet(XSSFSheet sheet, XSSFCellStyle headerStyle) {
        Row hRow = sheet.createRow(0);
        String[] refHeaders = {"Academic Sessions", "Classes & Sections", "Blood Groups", "Gender Options", "Guardian Relationship"};
        for (int i = 0; i < refHeaders.length; i++) {
            Cell cell = hRow.createCell(i);
            cell.setCellValue(refHeaders[i]);
            cell.setCellStyle(headerStyle);
        }

        List<AcademicSession> sessions = academicSessionRepository.findAll();
        List<ClassEntity> classes = classRepository.findAll();
        List<Section> sections = sectionRepository.findAll();

        String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        String[] genders = {"MALE", "FEMALE", "OTHER"};
        String[] relations = {"FATHER", "MOTHER", "GUARDIAN"};

        int maxRows = Math.max(sessions.size(), Math.max(sections.size(), bloodGroups.length));

        for (int i = 0; i < maxRows; i++) {
            Row row = sheet.createRow(i + 1);
            if (i < sessions.size()) {
                row.createCell(0).setCellValue(sessions.get(i).getName());
            }
            if (i < sections.size()) {
                Section sec = sections.get(i);
                String className = sec.getClassEntity() != null ? sec.getClassEntity().getName() : "Unknown";
                row.createCell(1).setCellValue(className + " -> " + sec.getName());
            }
            if (i < bloodGroups.length) {
                row.createCell(2).setCellValue(bloodGroups[i]);
            }
            if (i < genders.length) {
                row.createCell(3).setCellValue(genders[i]);
            }
            if (i < relations.length) {
                row.createCell(4).setCellValue(relations[i]);
            }
        }

        for (int i = 0; i < refHeaders.length; i++) {
            sheet.setColumnWidth(i, 25 * 256);
        }
    }

    private void populateStaffReferenceSheet(XSSFSheet sheet, XSSFCellStyle headerStyle) {
        Row hRow = sheet.createRow(0);
        String[] refHeaders = {"Departments", "Designations", "Categories", "Classes & Subjects"};
        for (int i = 0; i < refHeaders.length; i++) {
            Cell cell = hRow.createCell(i);
            cell.setCellValue(refHeaders[i]);
            cell.setCellStyle(headerStyle);
        }

        String[] depts = {"Science", "Mathematics", "English", "Urdu", "Islamiat", "Social Studies", "Administration", "Finance", "IT Support", "Library"};
        String[] desigs = {"Principal", "Vice Principal", "Senior Teacher", "Secondary Teacher", "Primary Teacher", "Lab Assistant", "Accounts Officer", "Librarian", "Administrative Assistant"};
        String[] cats = {"TEACHING", "NON_TEACHING", "ADMINISTRATIVE"};

        List<Subject> subjects = subjectRepository.findAll();

        int maxRows = Math.max(Math.max(depts.length, desigs.length), Math.max(cats.length, subjects.size()));

        for (int i = 0; i < maxRows; i++) {
            Row row = sheet.createRow(i + 1);
            if (i < depts.length) {
                row.createCell(0).setCellValue(depts[i]);
            }
            if (i < desigs.length) {
                row.createCell(1).setCellValue(desigs[i]);
            }
            if (i < cats.length) {
                row.createCell(2).setCellValue(cats[i]);
            }
            if (i < subjects.size()) {
                Subject sub = subjects.get(i);
                String className = sub.getClassEntity() != null ? sub.getClassEntity().getName() : "General";
                row.createCell(3).setCellValue(className + " -> " + sub.getName() + " (" + sub.getCode() + ")");
            }
        }

        for (int i = 0; i < refHeaders.length; i++) {
            sheet.setColumnWidth(i, 30 * 256);
        }
    }

    private void populateStudentInstructionsSheet(XSSFSheet sheet, XSSFCellStyle headerStyle) {
        Row hRow = sheet.createRow(0);
        String[] headers = {"Column Name", "Required?", "Format / Allowed Values", "Description & Rules"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = hRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        String[][] instructions = {
                {"admission_number", "YES", "e.g. ADM-2024-001", "Unique school admission identifier. Must not duplicate existing students."},
                {"first_name", "YES", "Letters and spaces", "Student's legal first name."},
                {"last_name", "YES", "Letters and spaces", "Student's family or last name."},
                {"gender", "YES", "MALE, FEMALE, OTHER", "Student gender from dropdown list."},
                {"date_of_birth", "YES", "YYYY-MM-DD", "Student birth date. Student must be at least 2 years old and not in the future."},
                {"blood_group", "NO", "A+, A-, B+, B-, AB+, AB-, O+, O-", "Medical blood group."},
                {"b_form_number", "NO", "42101-1234567-3 or digits", "National B-Form or CRC identification number."},
                {"residential_address", "YES", "Text (max 500 chars)", "Permanent or current residential address of the student."},
                {"admission_date", "YES", "YYYY-MM-DD", "Date on which the student officially registered at Bluebells."},
                {"academic_session", "YES", "e.g. 2026-2027", "Must match an existing Academic Session listed on Reference_Values sheet."},
                {"class_name", "YES", "e.g. Class 9", "Must match an existing Class in the school."},
                {"section_name", "YES", "e.g. Section A", "Must match a valid Section belonging to the specified Class."},
                {"roll_number", "NO", "e.g. 01, 15", "Student roll number inside the section. Unique per section/session."},
                {"guardian_name", "YES", "Letters and spaces", "Full name of the parent or primary guardian."},
                {"guardian_relation", "YES", "FATHER, MOTHER, GUARDIAN", "Legal relationship to student."},
                {"guardian_phone", "YES", "+92-3XX-XXXXXXX or 03XXXXXXXXX", "Contact mobile number. Used to resolve or link existing guardian accounts."},
                {"guardian_cnic", "NO", "42101-1234567-1 or 13 digits", "National Identity Card number of the guardian."},
                {"guardian_occupation", "NO", "Text (max 64 chars)", "Profession or occupation of the guardian."},
                {"guardian_address", "NO", "Text (max 500 chars)", "Guardian address (optional if same as student)."}
        };

        for (int i = 0; i < instructions.length; i++) {
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < instructions[i].length; j++) {
                row.createCell(j).setCellValue(instructions[i][j]);
            }
        }

        sheet.setColumnWidth(0, 22 * 256);
        sheet.setColumnWidth(1, 12 * 256);
        sheet.setColumnWidth(2, 28 * 256);
        sheet.setColumnWidth(3, 60 * 256);
    }

    private void populateStaffInstructionsSheet(XSSFSheet sheet, XSSFCellStyle headerStyle) {
        Row hRow = sheet.createRow(0);
        String[] headers = {"Column Name", "Required?", "Format / Allowed Values", "Description & Rules"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = hRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        String[][] instructions = {
                {"employee_id", "YES", "e.g. STF-2024-001", "Unique employee code. Must not duplicate existing staff records."},
                {"full_name", "YES", "Letters and spaces", "Staff member's legal full name."},
                {"gender", "YES", "MALE, FEMALE, OTHER", "Gender from dropdown list."},
                {"designation", "YES", "e.g. Senior Teacher, Accountant", "Designation or title held by staff member."},
                {"department", "YES", "e.g. Science, Mathematics, Admin", "Department within Bluebells Schooling System."},
                {"category", "YES", "TEACHING, NON_TEACHING, ADMINISTRATIVE", "Staff categorization. Controls role assignment."},
                {"qualification", "NO", "e.g. M.Sc, M.Ed, B.Sc", "Educational degrees and professional credentials."},
                {"joining_date", "YES", "YYYY-MM-DD", "Official employment commencement date."},
                {"official_email", "YES", "e.g. staff@bluebells.edu.pk", "Must be a valid email address and unique across all portal users."},
                {"phone_number", "YES", "+92-3XX-XXXXXXX or 03XXXXXXXXX", "Direct contact phone number."},
                {"cnic", "NO", "42101-1234567-1 or 13 digits", "National Identity Card number."},
                {"basic_salary", "NO", "Numeric, e.g. 65000.00", "Monthly base salary in PKR."},
                {"status", "YES", "ACTIVE, PROBATION, SUSPENDED", "Staff employment status."},
                {"teacher_subject_assignments", "NO", "Class|Section|Subject separated by ;", "For teachers: semicolon-separated assignments (e.g. 'Class 9|Section A|Mathematics;Class 10|Section A|Mathematics')."}
        };

        for (int i = 0; i < instructions.length; i++) {
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < instructions[i].length; j++) {
                row.createCell(j).setCellValue(instructions[i][j]);
            }
        }

        sheet.setColumnWidth(0, 26 * 256);
        sheet.setColumnWidth(1, 12 * 256);
        sheet.setColumnWidth(2, 32 * 256);
        sheet.setColumnWidth(3, 60 * 256);
    }
}
