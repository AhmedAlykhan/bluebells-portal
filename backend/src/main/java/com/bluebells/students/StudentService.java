package com.bluebells.students;

import com.bluebells.academics.AcademicSession;
import com.bluebells.academics.AcademicSessionRepository;
import com.bluebells.academics.Section;
import com.bluebells.academics.SectionRepository;
import com.bluebells.exception.ApiException;
import com.bluebells.exception.ResourceNotFoundException;
import com.bluebells.students.dto.CreateStudentRequest;
import com.bluebells.students.dto.StudentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final SectionRepository sectionRepository;
    private final AcademicSessionRepository sessionRepository;
    private final EnrollmentRepository enrollmentRepository;

    public StudentService(
            StudentRepository studentRepository,
            SectionRepository sectionRepository,
            AcademicSessionRepository sessionRepository,
            EnrollmentRepository enrollmentRepository) {
        this.studentRepository = studentRepository;
        this.sectionRepository = sectionRepository;
        this.sessionRepository = sessionRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Transactional(readOnly = true)
    public Page<StudentDto> getStudents(String query, Pageable pageable) {
        Page<Student> students;
        if (query != null && !query.trim().isEmpty()) {
            students = studentRepository.searchStudents(query.trim(), pageable);
        } else {
            students = studentRepository.findAll(pageable);
        }
        return students.map(StudentDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public StudentDto getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return StudentDto.fromEntity(student);
    }

    @Transactional(readOnly = true)
    public StudentDto getStudentByUserId(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No student profile found for user id: " + userId));
        return StudentDto.fromEntity(student);
    }

    @Transactional
    public StudentDto createStudent(CreateStudentRequest request) {
        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + request.getSectionId()));

        AcademicSession session;
        if (request.getSessionId() != null) {
            session = sessionRepository.findById(request.getSessionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + request.getSessionId()));
        } else {
            session = sessionRepository.findByIsCurrentTrue()
                    .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "No active academic session found"));
        }

        // Generate student admission number
        int currentYear = Year.now().getValue();
        long studentCount = studentRepository.count() + 1;
        String admissionNumber = String.format("BSS-%d-%04d", currentYear, studentCount);
        while (studentRepository.existsByAdmissionNumber(admissionNumber)) {
            studentCount++;
            admissionNumber = String.format("BSS-%d-%04d", currentYear, studentCount);
        }

        Student student = new Student();
        student.setAdmissionNumber(admissionNumber);
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setGender(request.getGender().toUpperCase());
        student.setBloodGroup(request.getBloodGroup());
        student.setBFormNumber(request.getBFormNumber());
        student.setResidentialAddress(request.getResidentialAddress());
        student.setAdmissionDate(request.getAdmissionDate() != null ? request.getAdmissionDate() : LocalDate.now());
        student.setStatus("ACTIVE");

        Student savedStudent = studentRepository.save(student);

        // Create Enrollment
        Enrollment enrollment = new Enrollment(
                savedStudent,
                session,
                section,
                request.getRollNumber(),
                request.getAdmissionDate() != null ? request.getAdmissionDate() : LocalDate.now()
        );
        enrollmentRepository.save(enrollment);

        savedStudent.getEnrollments().add(enrollment);

        return StudentDto.fromEntity(savedStudent);
    }

    @Transactional
    public StudentDto enrollStudent(Long studentId, Long sectionId, Long sessionId, String rollNumber) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));

        AcademicSession session;
        if (sessionId != null) {
            session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + sessionId));
        } else {
            session = sessionRepository.findByIsCurrentTrue()
                    .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "No active academic session found"));
        }

        if (enrollmentRepository.existsByStudentIdAndAcademicSessionId(studentId, session.getId())) {
            throw new ApiException(HttpStatus.CONFLICT, "Student is already enrolled in academic session: " + session.getName());
        }

        Enrollment enrollment = new Enrollment(student, session, section, rollNumber, LocalDate.now());
        enrollmentRepository.save(enrollment);
        student.getEnrollments().add(enrollment);

        return StudentDto.fromEntity(student);
    }
}
