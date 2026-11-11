package com.bluebells.development;

import com.bluebells.academics.AcademicSession;
import com.bluebells.academics.AcademicSessionRepository;
import com.bluebells.development.dto.CreateDevelopmentRecordRequest;
import com.bluebells.development.dto.StudentDevelopmentDto;
import com.bluebells.exception.ApiException;
import com.bluebells.guardians.Guardian;
import com.bluebells.guardians.GuardianRepository;
import com.bluebells.guardians.GuardianStudentLinkRepository;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
import com.bluebells.lms.FileStorageService;
import com.bluebells.security.UserPrincipal;
import com.bluebells.students.Student;
import com.bluebells.students.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentDevelopmentService {

    private final StudentDevelopmentRecordRepository recordRepository;
    private final StudentRepository studentRepository;
    private final AcademicSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final GuardianRepository guardianRepository;
    private final GuardianStudentLinkRepository guardianLinkRepository;
    private final FileStorageService fileStorageService;

    public StudentDevelopmentService(
            StudentDevelopmentRecordRepository recordRepository,
            StudentRepository studentRepository,
            AcademicSessionRepository sessionRepository,
            UserRepository userRepository,
            GuardianRepository guardianRepository,
            GuardianStudentLinkRepository guardianLinkRepository,
            FileStorageService fileStorageService) {
        this.recordRepository = recordRepository;
        this.studentRepository = studentRepository;
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.guardianRepository = guardianRepository;
        this.guardianLinkRepository = guardianLinkRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(readOnly = true)
    public List<StudentDevelopmentDto> getRecordsForStudent(Long studentId) {
        verifyReadAccessForStudent(studentId);
        return recordRepository.findByStudentIdOrderByAchievementDateDesc(studentId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public StudentDevelopmentDto createRecord(CreateDevelopmentRecordRequest req) {
        User currentUser = getCurrentUser();

        Student student = studentRepository.findById(req.getStudentId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Student not found with ID: " + req.getStudentId()));

        AcademicSession session;
        if (req.getAcademicSessionId() != null) {
            session = sessionRepository.findById(req.getAcademicSessionId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Academic Session not found"));
        } else {
            session = sessionRepository.findByCurrentTrue()
                    .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "No active academic session configured"));
        }

        StudentDevelopmentRecord record = new StudentDevelopmentRecord();
        record.setStudent(student);
        record.setAcademicSession(session);
        record.setCategory(req.getCategory().toUpperCase());
        record.setTitle(req.getTitle());
        record.setDescription(req.getDescription());
        record.setEventName(req.getEventName());
        record.setAchievementDate(req.getAchievementDate());
        record.setLevel(req.getLevel());
        record.setPositionOrAward(req.getPositionOrAward());
        record.setRecordedBy(currentUser);
        record.setTeacherAppreciationNote(req.getTeacherAppreciationNote());
        record.setSkillsAcquired(req.getSkillsAcquired());
        record.setStatus("VERIFIED");

        StudentDevelopmentRecord saved = recordRepository.save(record);
        return mapToDto(saved);
    }

    @Transactional
    public StudentDevelopmentDto uploadEvidence(Long recordId, MultipartFile file) {
        StudentDevelopmentRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Development record not found with ID: " + recordId));

        FileStorageService.StoredFile stored = fileStorageService.storeFile(file, "development");
        record.setEvidenceFilePath(stored.getRelativePath());
        record.setEvidenceFileName(stored.getOriginalFilename());

        StudentDevelopmentRecord saved = recordRepository.save(record);
        return mapToDto(saved);
    }

    @Transactional
    public void deleteRecord(Long recordId) {
        if (!recordRepository.existsById(recordId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Development record not found");
        }
        recordRepository.deleteById(recordId);
    }

    private void verifyReadAccessForStudent(Long studentId) {
        User currentUser = getCurrentUser();
        boolean isStaff = currentUser.getRoles().stream().anyMatch(r ->
                List.of("ROLE_SUPER_ADMIN", "ROLE_SCHOOL_ADMIN", "ROLE_PRINCIPAL", "ROLE_VICE_PRINCIPAL", "ROLE_TEACHER")
                        .contains(r.getName()));

        if (isStaff) {
            return; // Staff can view any student's development record
        }

        boolean isStudent = currentUser.getRoles().stream().anyMatch(r -> "ROLE_STUDENT".equalsIgnoreCase(r.getName()));
        if (isStudent) {
            Student student = studentRepository.findByUserId(currentUser.getId()).orElse(null);
            if (student == null || !student.getId().equals(studentId)) {
                throw new ApiException(HttpStatus.FORBIDDEN, "Access Denied: You may only view your own development achievements.");
            }
            return;
        }

        boolean isParent = currentUser.getRoles().stream().anyMatch(r -> "ROLE_PARENT".equalsIgnoreCase(r.getName()));
        if (isParent) {
            boolean isLinked = guardianLinkRepository.existsByGuardianUserIdAndStudentId(currentUser.getId(), studentId);
            if (!isLinked) {
                throw new ApiException(HttpStatus.FORBIDDEN, "Access Denied: You can only view achievements for your registered children.");
            }
            return;
        }

        throw new ApiException(HttpStatus.FORBIDDEN, "Access Denied: Unauthorized role for student development.");
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthenticated user");
        }
        String username;
        if (auth.getPrincipal() instanceof UserPrincipal) {
            username = ((UserPrincipal) auth.getPrincipal()).getUsername();
        } else {
            username = auth.getName();
        }
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private StudentDevelopmentDto mapToDto(StudentDevelopmentRecord r) {
        StudentDevelopmentDto dto = new StudentDevelopmentDto();
        dto.setId(r.getId());
        dto.setStudentId(r.getStudent().getId());
        dto.setStudentName(r.getStudent().getFirstName() + " " + r.getStudent().getLastName());
        dto.setAdmissionNumber(r.getStudent().getAdmissionNumber());
        dto.setAcademicSessionId(r.getAcademicSession().getId());
        dto.setSessionName(r.getAcademicSession().getName());
        dto.setCategory(r.getCategory());
        dto.setTitle(r.getTitle());
        dto.setDescription(r.getDescription());
        dto.setEventName(r.getEventName());
        dto.setAchievementDate(r.getAchievementDate());
        dto.setLevel(r.getLevel());
        dto.setPositionOrAward(r.getPositionOrAward());
        dto.setEvidenceFilePath(r.getEvidenceFilePath());
        dto.setEvidenceFileName(r.getEvidenceFileName());
        dto.setRecordedById(r.getRecordedBy().getId());
        dto.setRecordedByName(r.getRecordedBy().getFullName());
        dto.setTeacherAppreciationNote(r.getTeacherAppreciationNote());
        dto.setSkillsAcquired(r.getSkillsAcquired());
        dto.setStatus(r.getStatus());
        dto.setCreatedAt(r.getCreatedAt());
        return dto;
    }
}
