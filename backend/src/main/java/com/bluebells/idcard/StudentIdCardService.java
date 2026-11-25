package com.bluebells.idcard;

import com.bluebells.academics.AcademicSession;
import com.bluebells.academics.AcademicSessionRepository;
import com.bluebells.exception.ApiException;
import com.bluebells.guardians.Guardian;
import com.bluebells.guardians.GuardianRepository;
import com.bluebells.guardians.GuardianStudentLink;
import com.bluebells.guardians.GuardianStudentLinkRepository;
import com.bluebells.idcard.dto.GenerateIdCardRequest;
import com.bluebells.idcard.dto.ReissueIdCardRequest;
import com.bluebells.idcard.dto.StudentIdCardDto;
import com.bluebells.idcard.dto.VerifyIdCardResponse;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
import com.bluebells.security.UserPrincipal;
import com.bluebells.students.Enrollment;
import com.bluebells.students.EnrollmentRepository;
import com.bluebells.students.Student;
import com.bluebells.students.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StudentIdCardService {

    private final StudentIdCardRepository cardRepository;
    private final StudentRepository studentRepository;
    private final AcademicSessionRepository sessionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GuardianRepository guardianRepository;
    private final GuardianStudentLinkRepository guardianLinkRepository;
    private final UserRepository userRepository;

    public StudentIdCardService(
            StudentIdCardRepository cardRepository,
            StudentRepository studentRepository,
            AcademicSessionRepository sessionRepository,
            EnrollmentRepository enrollmentRepository,
            GuardianRepository guardianRepository,
            GuardianStudentLinkRepository guardianLinkRepository,
            UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.studentRepository = studentRepository;
        this.sessionRepository = sessionRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.guardianRepository = guardianRepository;
        this.guardianLinkRepository = guardianLinkRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public StudentIdCardDto getActiveCardForStudent(Long studentId) {
        verifyReadAccessForStudent(studentId);

        return cardRepository.findByStudentIdAndStatus(studentId, "ACTIVE")
                .map(this::mapToDto)
                .orElse(null);
    }

    @Transactional
    public StudentIdCardDto generateOrGetCard(GenerateIdCardRequest req) {
        StudentIdCard card = cardRepository.findByStudentIdAndStatus(req.getStudentId(), "ACTIVE")
                .orElseGet(() -> generateCardInternal(req.getStudentId(), req.getAcademicSessionId()));
        return mapToDto(card);
    }

    @Transactional
    public List<StudentIdCardDto> bulkGenerateForSection(Long sectionId, Long sessionId) {
        List<Enrollment> enrollments = enrollmentRepository.findBySectionIdAndAcademicSessionId(sectionId, sessionId);
        List<StudentIdCardDto> results = new ArrayList<>();

        for (Enrollment e : enrollments) {
            Student s = e.getStudent();
            StudentIdCard card = cardRepository.findByStudentIdAndStatus(s.getId(), "ACTIVE")
                    .orElseGet(() -> generateCardInternal(s.getId(), sessionId));
            results.add(mapToDto(card));
        }
        return results;
    }

    @Transactional
    public List<StudentIdCardDto> bulkGenerateAll(Long sessionId) {
        return bulkGenerateAll(sessionId, 100);
    }

    @Transactional
    public List<StudentIdCardDto> bulkGenerateAll(Long sessionId, int limit) {
        AcademicSession session;
        if (sessionId != null) {
            session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Academic Session not found"));
        } else {
            session = sessionRepository.findByCurrentTrue()
                    .orElseGet(() -> sessionRepository.findAll().stream().findFirst()
                            .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "No active academic session found")));
        }

        int safeLimit = Math.min(Math.max(1, limit), 200);
        org.springframework.data.domain.Page<Student> page = studentRepository.findAll(
                org.springframework.data.domain.PageRequest.of(0, safeLimit, org.springframework.data.domain.Sort.by("id").ascending()));
        List<Student> students = page.getContent();
        List<StudentIdCardDto> results = new ArrayList<>();
        for (Student s : students) {
            StudentIdCard card = cardRepository.findByStudentIdAndStatus(s.getId(), "ACTIVE")
                    .orElseGet(() -> generateCardInternal(s.getId(), session.getId()));
            results.add(mapToDto(card));
        }
        return results;
    }

    @Transactional
    public StudentIdCardDto reissueCard(Long cardId, ReissueIdCardRequest req) {
        User currentUser = getCurrentUser();

        StudentIdCard oldCard = cardRepository.findById(cardId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Card not found with ID: " + cardId));

        oldCard.setStatus("REISSUED");
        cardRepository.save(oldCard);

        // Generate replacement card
        Student student = oldCard.getStudent();
        AcademicSession session = oldCard.getAcademicSession();

        StudentIdCard newCard = new StudentIdCard();
        newCard.setStudent(student);
        newCard.setAcademicSession(session);
        newCard.setCardNumber("IDC-" + LocalDate.now().getYear() + "-" + String.format("%04d", student.getId()) + "-R" + (oldCard.getReissueCount() + 1));
        newCard.setIssueDate(LocalDate.now());
        newCard.setExpiryDate(session.getEndDate() != null ? session.getEndDate() : LocalDate.now().plusYears(1));
        newCard.setStatus("ACTIVE");
        newCard.setReissueCount(oldCard.getReissueCount() + 1);
        newCard.setReissueReason(req.getReason());
        newCard.setReissuedAt(Instant.now());
        newCard.setReissuedBy(currentUser);
        newCard.setQrVerificationToken("BSS-ID-TOKEN-" + UUID.randomUUID().toString());

        StudentIdCard saved = cardRepository.save(newCard);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public VerifyIdCardResponse verifyCardToken(String token) {
        StudentIdCard card = cardRepository.findByQrVerificationToken(token).orElse(null);
        if (card == null) {
            return VerifyIdCardResponse.invalid("Invalid QR Verification Code. This ID card is not authentic or has been invalidated.");
        }

        if (!"ACTIVE".equalsIgnoreCase(card.getStatus())) {
            return VerifyIdCardResponse.invalid("This ID Card is no longer active (Status: " + card.getStatus() + "). Please present the reissued official card.");
        }

        if (card.getExpiryDate() != null && card.getExpiryDate().isBefore(LocalDate.now())) {
            return VerifyIdCardResponse.invalid("This ID Card expired on " + card.getExpiryDate() + ".");
        }

        Student s = card.getStudent();
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(s.getId());
        String cName = !enrollments.isEmpty() && enrollments.get(0).getSection() != null && enrollments.get(0).getSection().getClassEntity() != null
                ? enrollments.get(0).getSection().getClassEntity().getName() : "Bluebells Student";

        return VerifyIdCardResponse.valid(
                card.getCardNumber(),
                s.getFirstName() + " " + s.getLastName(),
                s.getAdmissionNumber(),
                cName,
                card.getAcademicSession().getName(),
                card.getIssueDate(),
                card.getExpiryDate(),
                card.getStatus()
        );
    }

    private StudentIdCard generateCardInternal(Long studentId, Long sessionId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Student not found with ID: " + studentId));

        AcademicSession session;
        if (sessionId != null) {
            session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Academic Session not found"));
        } else {
            session = sessionRepository.findByCurrentTrue()
                    .orElseGet(() -> sessionRepository.findAll().stream().findFirst()
                            .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "No active academic session found")));
        }

        String baseCardNumber = "IDC-" + LocalDate.now().getYear() + "-" + String.format("%04d", student.getId());
        String cardNumber = baseCardNumber;
        int suffix = 1;
        while (cardRepository.findByCardNumber(cardNumber).isPresent()) {
            cardNumber = baseCardNumber + "-" + suffix++;
        }

        StudentIdCard card = new StudentIdCard();
        card.setStudent(student);
        card.setAcademicSession(session);
        card.setCardNumber(cardNumber);
        card.setIssueDate(LocalDate.now());
        card.setExpiryDate(session.getEndDate() != null ? session.getEndDate() : LocalDate.now().plusYears(1));
        card.setStatus("ACTIVE");
        card.setReissueCount(0);
        card.setQrVerificationToken("BSS-ID-TOKEN-" + UUID.randomUUID().toString());

        return cardRepository.save(card);
    }

    private void verifyReadAccessForStudent(Long studentId) {
        User currentUser = getCurrentUser();
        boolean isStaff = currentUser.getRoles().stream().anyMatch(r ->
                List.of("ROLE_SUPER_ADMIN", "ROLE_SCHOOL_ADMIN", "ROLE_PRINCIPAL", "ROLE_VICE_PRINCIPAL", "ROLE_TEACHER", "ROLE_ACCOUNTANT")
                        .contains(r.getName()));

        if (isStaff) return;

        boolean isStudent = currentUser.getRoles().stream().anyMatch(r -> "ROLE_STUDENT".equalsIgnoreCase(r.getName()));
        if (isStudent) {
            Student student = studentRepository.findByUserId(currentUser.getId()).orElse(null);
            if (student == null || !student.getId().equals(studentId)) {
                throw new ApiException(HttpStatus.FORBIDDEN, "Access Denied: You may only view your own ID card.");
            }
            return;
        }

        boolean isParent = currentUser.getRoles().stream().anyMatch(r -> "ROLE_PARENT".equalsIgnoreCase(r.getName()));
        if (isParent) {
            boolean isLinked = guardianLinkRepository.existsByGuardianUserIdAndStudentId(currentUser.getId(), studentId);
            if (!isLinked) {
                throw new ApiException(HttpStatus.FORBIDDEN, "Access Denied: You can only view ID cards for your registered children.");
            }
            return;
        }

        throw new ApiException(HttpStatus.FORBIDDEN, "Access Denied: Unauthorized role for student ID card.");
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthenticated user");
        }
        String username = auth.getPrincipal() instanceof UserPrincipal ? ((UserPrincipal) auth.getPrincipal()).getUsername() : auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private StudentIdCardDto mapToDto(StudentIdCard c) {
        Student s = c.getStudent();
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(s.getId());
        String cName = "Not Enrolled";
        String secName = "Not Assigned";
        String roll = "N/A";
        if (!enrollments.isEmpty()) {
            Enrollment e = enrollments.get(0);
            if (e.getSection() != null) {
                secName = e.getSection().getName();
                if (e.getSection().getClassEntity() != null) {
                    cName = e.getSection().getClassEntity().getName();
                }
            }
            if (e.getRollNumber() != null && !e.getRollNumber().isBlank()) {
                roll = e.getRollNumber();
            }
        }

        // Guardian / Father Name
        List<GuardianStudentLink> links = guardianLinkRepository.findByStudentId(s.getId());
        String fatherName = "N/A";
        if (!links.isEmpty() && links.get(0).getGuardian() != null) {
            fatherName = links.get(0).getGuardian().getFullName();
        }

        StudentIdCardDto dto = new StudentIdCardDto();
        dto.setId(c.getId());
        dto.setStudentId(s.getId());
        dto.setAdmissionNumber(s.getAdmissionNumber());
        dto.setStudentName(s.getFirstName() + " " + s.getLastName());
        dto.setFatherName(fatherName);
        dto.setClassName(cName);
        dto.setSectionName(secName);
        dto.setRollNumber(roll);
        dto.setAcademicSession(c.getAcademicSession().getName());
        // SEC-11: Never fabricate medical blood group — return actual blood group or "N/A"
        dto.setBloodGroup(s.getBloodGroup() != null && !s.getBloodGroup().isBlank() ? s.getBloodGroup() : "N/A");
        dto.setEmergencyContact((s.getUser() != null && s.getUser().getPhoneNumber() != null && !s.getUser().getPhoneNumber().isBlank())
                ? s.getUser().getPhoneNumber()
                : "+92 316 9681932");
        dto.setCardNumber(c.getCardNumber());
        dto.setIssueDate(c.getIssueDate());
        dto.setExpiryDate(c.getExpiryDate());
        dto.setStatus(c.getStatus());
        dto.setReissueCount(c.getReissueCount());
        dto.setQrVerificationToken(c.getQrVerificationToken());

        // Safe QR payload containing NO personal information
        dto.setQrPayload("https://portal.bluebells.edu.pk/verify-id?card=" + c.getCardNumber() + "&sig=" + c.getQrVerificationToken());

        dto.setSchoolAddress("Adjacent Autobahn Road, Next to VIP Garden, Unit 2, Latifabad, Hyderabad, Sindh");
        dto.setSchoolPhone("+92 316 9681932");
        dto.setPrincipalName("Huma Arif");

        return dto;
    }
}
