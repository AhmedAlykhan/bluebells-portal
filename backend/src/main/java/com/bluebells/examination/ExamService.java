package com.bluebells.examination;

import com.bluebells.academics.AcademicSession;
import com.bluebells.academics.AcademicSessionRepository;
import com.bluebells.academics.Subject;
import com.bluebells.academics.SubjectRepository;
import com.bluebells.academics.Term;
import com.bluebells.academics.TermRepository;
import com.bluebells.attendance.AttendanceService;
import com.bluebells.examination.dto.*;
import com.bluebells.exception.ApiException;
import com.bluebells.exception.ResourceNotFoundException;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
import com.bluebells.security.UserPrincipal;
import com.bluebells.students.Enrollment;
import com.bluebells.students.EnrollmentRepository;
import com.bluebells.students.Student;
import com.bluebells.students.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExamService {

    private final ExamRepository examRepository;
    private final GradingScaleRepository gradingScaleRepository;
    private final MarksEntryRepository marksEntryRepository;
    private final ReportCardRepository reportCardRepository;
    private final AcademicSessionRepository sessionRepository;
    private final TermRepository termRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final AttendanceService attendanceService;

    public ExamService(
            ExamRepository examRepository,
            GradingScaleRepository gradingScaleRepository,
            MarksEntryRepository marksEntryRepository,
            ReportCardRepository reportCardRepository,
            AcademicSessionRepository sessionRepository,
            TermRepository termRepository,
            SubjectRepository subjectRepository,
            StudentRepository studentRepository,
            EnrollmentRepository enrollmentRepository,
            UserRepository userRepository,
            AttendanceService attendanceService) {
        this.examRepository = examRepository;
        this.gradingScaleRepository = gradingScaleRepository;
        this.marksEntryRepository = marksEntryRepository;
        this.reportCardRepository = reportCardRepository;
        this.sessionRepository = sessionRepository;
        this.termRepository = termRepository;
        this.subjectRepository = subjectRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.attendanceService = attendanceService;
    }

    @Transactional
    public ExamDto createExam(CreateExamRequest request) {
        AcademicSession session;
        if (request.getSessionId() != null) {
            session = sessionRepository.findById(request.getSessionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + request.getSessionId()));
        } else {
            session = sessionRepository.findByCurrentTrue()
                    .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "No active academic session found"));
        }

        Term term = termRepository.findById(request.getTermId())
                .orElseThrow(() -> new ResourceNotFoundException("Term not found with id: " + request.getTermId()));

        Exam exam = new Exam(session, term, request.getName(), request.getStartDate(), request.getEndDate());
        Exam saved = examRepository.save(exam);
        return ExamDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<ExamDto> getExams() {
        return examRepository.findAll().stream().map(ExamDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExamDto getExamById(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + id));
        return ExamDto.fromEntity(exam);
    }

    @Transactional
    public List<MarksEntryDto> recordMarks(RecordMarksRequest request, UserPrincipal currentUser) {
        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + request.getExamId()));

        if ("PUBLISHED".equalsIgnoreCase(exam.getStatus())) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Exam results have been published; marks entries are locked and immutable.");
        }

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + request.getSubjectId()));

        User recorder = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUser.getId()));

        List<MarksEntry> savedEntries = new ArrayList<>();

        for (EnterMarksItemRequest item : request.getItems()) {
            Student student = studentRepository.findById(item.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + item.getStudentId()));

            BigDecimal maxMarks = item.getMaxMarks() != null ? item.getMaxMarks() : new BigDecimal("100.00");
            BigDecimal marksObtained = item.isAbsent() ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : item.getMarksObtained();

            if (marksObtained.compareTo(maxMarks) > 0) {
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        String.format("Marks obtained (%s) cannot exceed maximum marks (%s) for student %s",
                                marksObtained, maxMarks, student.getFullName()));
            }

            String grade;
            if (item.isAbsent()) {
                grade = "ABSENT";
            } else {
                BigDecimal percentage = marksObtained
                        .divide(maxMarks, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);
                grade = determineGrade(percentage);
            }

            Optional<MarksEntry> existingOpt = marksEntryRepository.findByExamIdAndSubjectIdAndStudentId(
                    exam.getId(), subject.getId(), student.getId());

            MarksEntry entry;
            if (existingOpt.isPresent()) {
                entry = existingOpt.get();
                entry.setMarksObtained(marksObtained);
                entry.setMaxMarks(maxMarks);
                entry.setGrade(grade);
                entry.setAbsent(item.isAbsent());
                entry.setRecordedBy(recorder);
            } else {
                entry = new MarksEntry(exam, subject, student, marksObtained, maxMarks, grade, item.isAbsent(), recorder);
            }

            savedEntries.add(marksEntryRepository.save(entry));
        }

        return savedEntries.stream().map(MarksEntryDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public ExamDto publishExamResults(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));

        List<MarksEntry> allMarks = marksEntryRepository.findByExamId(examId);
        if (allMarks.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot publish results: No marks entries recorded for this exam");
        }

        Map<Student, List<MarksEntry>> marksByStudent = allMarks.stream()
                .collect(Collectors.groupingBy(MarksEntry::getStudent));

        List<ReportCard> generatedReportCards = new ArrayList<>();

        for (Map.Entry<Student, List<MarksEntry>> entry : marksByStudent.entrySet()) {
            Student student = entry.getKey();
            List<MarksEntry> studentMarks = entry.getValue();

            BigDecimal totalObtained = BigDecimal.ZERO;
            BigDecimal totalMax = BigDecimal.ZERO;

            for (MarksEntry m : studentMarks) {
                totalObtained = totalObtained.add(m.getMarksObtained());
                totalMax = totalMax.add(m.getMaxMarks());
            }

            BigDecimal overallPct = BigDecimal.ZERO;
            if (totalMax.compareTo(BigDecimal.ZERO) > 0) {
                overallPct = totalObtained.divide(totalMax, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);
            }

            String overallGrade = determineGrade(overallPct);

            List<Enrollment> enrollments = enrollmentRepository.findByStudentId(student.getId());
            Enrollment enrollment = enrollments.stream()
                    .filter(e -> "ACTIVE".equalsIgnoreCase(e.getEnrollmentStatus()))
                    .findFirst()
                    .orElseGet(() -> enrollments.isEmpty() ? null : enrollments.get(0));

            if (enrollment == null) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Student " + student.getAdmissionNumber() + " has no active enrollment record");
            }

            LocalDate startDate = exam.getTerm() != null && exam.getTerm().getStartDate() != null ?
                    exam.getTerm().getStartDate() : exam.getStartDate();
            LocalDate endDate = exam.getTerm() != null && exam.getTerm().getEndDate() != null ?
                    exam.getTerm().getEndDate() : exam.getEndDate();

            BigDecimal attPct = new BigDecimal("100.00");
            if (startDate != null && endDate != null) {
                var attSummary = attendanceService.getStudentAttendanceSummary(student.getId(), startDate, endDate);
                if (attSummary.getTotalDays() > 0) {
                    attPct = BigDecimal.valueOf(attSummary.getAttendancePercentage()).setScale(2, RoundingMode.HALF_UP);
                }
            }

            Optional<ReportCard> existingReport = reportCardRepository.findByExamIdAndStudentId(examId, student.getId());
            ReportCard rc = existingReport.orElseGet(ReportCard::new);
            rc.setExam(exam);
            rc.setStudent(student);
            rc.setEnrollment(enrollment);
            rc.setTotalMarksObtained(totalObtained.setScale(2, RoundingMode.HALF_UP));
            rc.setTotalMaxMarks(totalMax.setScale(2, RoundingMode.HALF_UP));
            rc.setOverallPercentage(overallPct);
            rc.setOverallGrade(overallGrade);
            rc.setAttendancePercentage(attPct);
            rc.setStatus("PUBLISHED");

            generatedReportCards.add(rc);
        }

        // Compute class ranks partitioned by section based on overallPercentage descending
        Map<Long, List<ReportCard>> cardsBySection = generatedReportCards.stream()
                .collect(Collectors.groupingBy(rc -> rc.getEnrollment().getSection().getId()));

        for (List<ReportCard> sectionCards : cardsBySection.values()) {
            sectionCards.sort(Comparator.comparing(ReportCard::getOverallPercentage).reversed());
            for (int i = 0; i < sectionCards.size(); i++) {
                sectionCards.get(i).setClassRank(i + 1);
            }
        }

        reportCardRepository.saveAll(generatedReportCards);

        exam.setStatus("PUBLISHED");
        Exam savedExam = examRepository.save(exam);

        return ExamDto.fromEntity(savedExam);
    }

    @Transactional(readOnly = true)
    public ReportCardDto getReportCard(Long examId, Long studentId) {
        ReportCard rc = reportCardRepository.findByExamIdAndStudentId(examId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Report card not found for exam %d and student %d", examId, studentId)));

        ReportCardDto dto = ReportCardDto.fromEntity(rc);

        List<MarksEntry> studentMarks = marksEntryRepository.findByExamIdAndStudentId(examId, studentId);
        List<SubjectMarkDto> subjectDtos = studentMarks.stream().map(m -> {
            BigDecimal pct = BigDecimal.ZERO;
            if (m.getMaxMarks() != null && m.getMaxMarks().compareTo(BigDecimal.ZERO) > 0) {
                pct = m.getMarksObtained().divide(m.getMaxMarks(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);
            }
            return new SubjectMarkDto(
                    m.getSubject().getCode(),
                    m.getSubject().getName(),
                    m.getMarksObtained(),
                    m.getMaxMarks(),
                    pct,
                    m.getGrade(),
                    m.isAbsent() ? "Absent" : "Appeared"
            );
        }).collect(Collectors.toList());

        dto.setSubjectMarks(subjectDtos);
        return dto;
    }

    @Transactional(readOnly = true)
    public List<ReportCardDto> getReportCardsForStudent(Long studentId) {
        List<ReportCard> reportCards = reportCardRepository.findByStudentId(studentId);
        return reportCards.stream().map(rc -> {
            ReportCardDto dto = ReportCardDto.fromEntity(rc);
            List<MarksEntry> studentMarks = marksEntryRepository.findByExamIdAndStudentId(rc.getExam().getId(), studentId);
            List<SubjectMarkDto> subjectDtos = studentMarks.stream().map(m -> {
                BigDecimal pct = BigDecimal.ZERO;
                if (m.getMaxMarks() != null && m.getMaxMarks().compareTo(BigDecimal.ZERO) > 0) {
                    pct = m.getMarksObtained().divide(m.getMaxMarks(), 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .setScale(2, RoundingMode.HALF_UP);
                }
                return new SubjectMarkDto(
                        m.getSubject().getCode(),
                        m.getSubject().getName(),
                        m.getMarksObtained(),
                        m.getMaxMarks(),
                        pct,
                        m.getGrade(),
                        m.isAbsent() ? "Absent" : "Appeared"
                );
            }).collect(Collectors.toList());
            dto.setSubjectMarks(subjectDtos);
            return dto;
        }).collect(Collectors.toList());
    }

    private String determineGrade(BigDecimal percentage) {
        return gradingScaleRepository.findScaleForPercentage(percentage)
                .map(GradingScale::getGrade)
                .orElseGet(() -> {
                    if (percentage.compareTo(new BigDecimal("80.00")) >= 0) return "A+";
                    if (percentage.compareTo(new BigDecimal("70.00")) >= 0) return "A";
                    if (percentage.compareTo(new BigDecimal("60.00")) >= 0) return "B";
                    if (percentage.compareTo(new BigDecimal("50.00")) >= 0) return "C";
                    if (percentage.compareTo(new BigDecimal("40.00")) >= 0) return "D";
                    return "F";
                });
    }
}
