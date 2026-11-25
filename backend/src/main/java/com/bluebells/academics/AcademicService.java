package com.bluebells.academics;

import com.bluebells.academics.dto.*;
import com.bluebells.exception.ApiException;
import com.bluebells.exception.ResourceNotFoundException;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
import com.bluebells.staff.TeacherAssignment;
import com.bluebells.staff.TeacherAssignmentRepository;
import com.bluebells.staff.dto.CreateTeacherAssignmentRequest;
import com.bluebells.staff.dto.TeacherAssignmentDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AcademicService {

    private final AcademicSessionRepository sessionRepository;
    private final TermRepository termRepository;
    private final GradeLevelRepository gradeLevelRepository;
    private final ClassRepository classRepository;
    private final SectionRepository sectionRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherAssignmentRepository teacherAssignmentRepository;
    private final UserRepository userRepository;

    public AcademicService(
            AcademicSessionRepository sessionRepository,
            TermRepository termRepository,
            GradeLevelRepository gradeLevelRepository,
            ClassRepository classRepository,
            SectionRepository sectionRepository,
            SubjectRepository subjectRepository,
            TeacherAssignmentRepository teacherAssignmentRepository,
            UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.termRepository = termRepository;
        this.gradeLevelRepository = gradeLevelRepository;
        this.classRepository = classRepository;
        this.sectionRepository = sectionRepository;
        this.subjectRepository = subjectRepository;
        this.teacherAssignmentRepository = teacherAssignmentRepository;
        this.userRepository = userRepository;
    }

    // Sessions & Terms
    @Transactional(readOnly = true)
    @org.springframework.cache.annotation.Cacheable("academicSessions")
    public List<AcademicSessionDto> getSessions() {
        return sessionRepository.findAll().stream()
                .map(AcademicSessionDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @org.springframework.cache.annotation.Cacheable("academicSessions")
    public AcademicSessionDto getCurrentSession() {
        AcademicSession session = sessionRepository.findByCurrentTrue()
                .orElseThrow(() -> new ResourceNotFoundException("No active academic session configured."));
        return AcademicSessionDto.fromEntity(session);
    }

    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "academicSessions", allEntries = true)
    public AcademicSessionDto createSession(CreateAcademicSessionRequest request) {
        if (sessionRepository.existsByName(request.getName())) {
            throw new ApiException("Academic session '" + request.getName() + "' already exists", HttpStatus.CONFLICT);
        }

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new ApiException("Session start date must precede end date", HttpStatus.BAD_REQUEST);
        }

        AcademicSession session = new AcademicSession(
                request.getName(),
                request.getStartDate(),
                request.getEndDate(),
                request.isCurrent()
        );

        AcademicSession saved = sessionRepository.save(session);
        if (request.isCurrent()) {
            sessionRepository.resetOtherSessionsCurrentStatus(saved.getId());
        }

        return AcademicSessionDto.fromEntity(saved);
    }

    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "academicSessions", allEntries = true)
    public AcademicSessionDto setCurrentSession(Long sessionId) {
        AcademicSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("AcademicSession", "id", sessionId));

        session.setCurrent(true);
        sessionRepository.save(session);
        sessionRepository.resetOtherSessionsCurrentStatus(sessionId);

        return AcademicSessionDto.fromEntity(session);
    }

    @Transactional(readOnly = true)
    public List<TermDto> getTerms(Long sessionId) {
        return termRepository.findByAcademicSessionId(sessionId).stream()
                .map(TermDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public TermDto createTerm(Long sessionId, CreateTermRequest request) {
        AcademicSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("AcademicSession", "id", sessionId));

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new ApiException("Term start date must precede end date", HttpStatus.BAD_REQUEST);
        }

        // Validate term dates fall within the academic session boundary
        if (session.getStartDate() != null && session.getEndDate() != null) {
            if (request.getStartDate().isBefore(session.getStartDate()) || request.getEndDate().isAfter(session.getEndDate())) {
                throw new ApiException(
                        String.format("Term dates (%s to %s) must fall within academic session boundaries (%s to %s)",
                                request.getStartDate(), request.getEndDate(), session.getStartDate(), session.getEndDate()),
                        HttpStatus.BAD_REQUEST);
            }
        }

        // Validate that term date range does not overlap with existing terms in the session
        List<Term> existingTerms = termRepository.findByAcademicSessionId(sessionId);
        for (Term t : existingTerms) {
            boolean overlaps = !(request.getEndDate().isBefore(t.getStartDate()) || request.getStartDate().isAfter(t.getEndDate()));
            if (overlaps) {
                throw new ApiException(
                        String.format("Term date range (%s to %s) overlaps with existing term '%s' (%s to %s)",
                                request.getStartDate(), request.getEndDate(), t.getName(), t.getStartDate(), t.getEndDate()),
                        HttpStatus.CONFLICT);
            }
        }

        // Validate cumulative weight does not exceed 100%
        java.math.BigDecimal totalWeight = existingTerms.stream()
                .map(Term::getWeightPercentage)
                .filter(java.util.Objects::nonNull)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        java.math.BigDecimal newTotal = request.getWeightPercentage() != null ? totalWeight.add(request.getWeightPercentage()) : totalWeight;
        if (newTotal.compareTo(new java.math.BigDecimal("100.00")) > 0) {
            throw new ApiException(
                    String.format("Cumulative term weight percentage (%s%%) cannot exceed 100%%", newTotal),
                    HttpStatus.BAD_REQUEST);
        }

        Term term = new Term(
                session,
                request.getName(),
                request.getStartDate(),
                request.getEndDate(),
                request.getWeightPercentage()
        );

        Term saved = termRepository.save(term);
        return TermDto.fromEntity(saved);
    }

    // Grade Levels, Classes, Sections, Subjects
    @Transactional(readOnly = true)
    public List<GradeLevelDto> getGradeLevels() {
        return gradeLevelRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(GradeLevelDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClassDto> getClasses() {
        return classRepository.findAll().stream()
                .map(ClassDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClassDto createClass(CreateClassRequest request) {
        if (classRepository.existsByCode(request.getCode())) {
            throw new ApiException("Class with code '" + request.getCode() + "' already exists", HttpStatus.CONFLICT);
        }

        GradeLevel gradeLevel = gradeLevelRepository.findById(request.getGradeLevelId())
                .orElseThrow(() -> new ResourceNotFoundException("GradeLevel", "id", request.getGradeLevelId()));

        ClassEntity classEntity = new ClassEntity(gradeLevel, request.getName(), request.getCode());
        ClassEntity saved = classRepository.save(classEntity);
        return ClassDto.fromEntity(saved);
    }

    @Transactional
    public SectionDto createSection(Long classId, CreateSectionRequest request) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("ClassEntity", "id", classId));

        if (sectionRepository.existsByClassEntityIdAndName(classId, request.getName())) {
            throw new ApiException("Section '" + request.getName() + "' already exists for this class", HttpStatus.CONFLICT);
        }

        Section section = new Section(classEntity, request.getName(), request.getRoomNumber(), request.getMaxCapacity());
        Section saved = sectionRepository.save(section);
        return SectionDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<SubjectDto> getSubjects(Long classId) {
        return subjectRepository.findByClassEntityId(classId).stream()
                .map(SubjectDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public SubjectDto createSubject(Long classId, CreateSubjectRequest request) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("ClassEntity", "id", classId));

        if (subjectRepository.existsByClassEntityIdAndCode(classId, request.getCode())) {
            throw new ApiException("Subject with code '" + request.getCode() + "' already exists in this class", HttpStatus.CONFLICT);
        }

        Subject subject = new Subject(classEntity, request.getName(), request.getCode(), request.isElective(), request.getTotalMarks());
        Subject saved = subjectRepository.save(subject);
        return SubjectDto.fromEntity(saved);
    }

    // Teacher Assignments
    @Transactional(readOnly = true)
    public List<TeacherAssignmentDto> getTeacherAssignments(Long sessionId) {
        Long targetSessionId = sessionId;
        if (targetSessionId == null) {
            targetSessionId = sessionRepository.findByCurrentTrue()
                    .map(AcademicSession::getId)
                    .orElse(1L);
        }
        return teacherAssignmentRepository.findByAcademicSessionId(targetSessionId).stream()
                .map(TeacherAssignmentDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public TeacherAssignmentDto createTeacherAssignment(CreateTeacherAssignmentRequest request) {
        final Long sessionId = request.getSessionId() != null
                ? request.getSessionId()
                : sessionRepository.findByCurrentTrue()
                        .map(AcademicSession::getId)
                        .orElseThrow(() -> new ApiException("No active academic session found", HttpStatus.BAD_REQUEST));

        AcademicSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("AcademicSession", "id", sessionId));

        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher User", "id", request.getTeacherId()));

        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section", "id", request.getSectionId()));

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.getSubjectId()));

        if (teacherAssignmentRepository.existsByTeacherIdAndSectionIdAndSubjectIdAndAcademicSessionId(
                teacher.getId(), section.getId(), subject.getId(), session.getId())) {
            throw new ApiException("Teacher is already assigned to this section and subject for session " + session.getName(), HttpStatus.CONFLICT);
        }

        TeacherAssignment assignment = new TeacherAssignment(
                teacher,
                section,
                subject,
                session,
                request.isClassTeacher()
        );

        TeacherAssignment saved = teacherAssignmentRepository.save(assignment);
        return TeacherAssignmentDto.fromEntity(saved);
    }
}
