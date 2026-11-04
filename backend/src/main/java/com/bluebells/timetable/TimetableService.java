package com.bluebells.timetable;

import com.bluebells.academics.AcademicSession;
import com.bluebells.academics.AcademicSessionRepository;
import com.bluebells.academics.Section;
import com.bluebells.academics.SectionRepository;
import com.bluebells.academics.Subject;
import com.bluebells.academics.SubjectRepository;
import com.bluebells.exception.ApiException;
import com.bluebells.exception.ResourceNotFoundException;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
import com.bluebells.timetable.dto.CreateTimetableSlotRequest;
import com.bluebells.timetable.dto.TimetableSlotDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bluebells.guardians.GuardianService;
import com.bluebells.security.UserPrincipal;
import com.bluebells.students.Enrollment;
import com.bluebells.students.EnrollmentRepository;
import com.bluebells.students.Student;
import com.bluebells.students.StudentRepository;
import com.bluebells.students.dto.StudentDto;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TimetableService {

    private final TimetableRepository timetableRepository;
    private final AcademicSessionRepository sessionRepository;
    private final SectionRepository sectionRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GuardianService guardianService;

    public TimetableService(
            TimetableRepository timetableRepository,
            AcademicSessionRepository sessionRepository,
            SectionRepository sectionRepository,
            SubjectRepository subjectRepository,
            UserRepository userRepository,
            StudentRepository studentRepository,
            EnrollmentRepository enrollmentRepository,
            GuardianService guardianService) {
        this.timetableRepository = timetableRepository;
        this.sessionRepository = sessionRepository;
        this.sectionRepository = sectionRepository;
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.guardianService = guardianService;
    }

    @Transactional
    public TimetableSlotDto createSlot(CreateTimetableSlotRequest request) {
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Start time must be before end time");
        }

        AcademicSession session;
        if (request.getSessionId() != null) {
            session = sessionRepository.findById(request.getSessionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Academic session not found with id: " + request.getSessionId()));
        } else {
            session = sessionRepository.findByCurrentTrue()
                    .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "No active academic session found"));
        }

        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + request.getSectionId()));

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + request.getSubjectId()));

        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + request.getTeacherId()));

        String day = request.getDayOfWeek().toUpperCase();

        // 1. Check teacher clash
        List<TimetableSlot> teacherClashes = timetableRepository.findTeacherClashes(
                teacher.getId(), day, request.getStartTime(), request.getEndTime());
        if (!teacherClashes.isEmpty()) {
            TimetableSlot clash = teacherClashes.get(0);
            throw new ApiException(HttpStatus.CONFLICT,
                    String.format("Teacher schedule conflict: %s is already scheduled for %s in %s from %s to %s",
                            teacher.getFullName(), clash.getSubject().getName(), clash.getSection().getName(),
                            clash.getStartTime(), clash.getEndTime()));
        }

        // 2. Check section clash
        List<TimetableSlot> sectionClashes = timetableRepository.findSectionClashes(
                section.getId(), day, request.getStartTime(), request.getEndTime());
        if (!sectionClashes.isEmpty()) {
            TimetableSlot clash = sectionClashes.get(0);
            throw new ApiException(HttpStatus.CONFLICT,
                    String.format("Section schedule conflict: %s already has %s scheduled from %s to %s",
                            section.getName(), clash.getSubject().getName(), clash.getStartTime(), clash.getEndTime()));
        }

        // 3. Check room clash
        if (request.getRoomNumber() != null && !request.getRoomNumber().trim().isEmpty()) {
            List<TimetableSlot> roomClashes = timetableRepository.findRoomClashes(
                    request.getRoomNumber().trim(), day, request.getStartTime(), request.getEndTime());
            if (!roomClashes.isEmpty()) {
                TimetableSlot clash = roomClashes.get(0);
                throw new ApiException(HttpStatus.CONFLICT,
                        String.format("Room conflict: Room %s is already booked for %s (%s) from %s to %s",
                                request.getRoomNumber().trim(), clash.getSection().getName(), clash.getSubject().getName(),
                                clash.getStartTime(), clash.getEndTime()));
            }
        }

        TimetableSlot slot = new TimetableSlot(
                session, section, subject, teacher,
                day, request.getStartTime(), request.getEndTime(),
                request.getRoomNumber() != null ? request.getRoomNumber().trim() : null
        );

        TimetableSlot saved = timetableRepository.save(slot);
        return TimetableSlotDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<TimetableSlotDto> getTimetableForSection(Long sectionId) {
        if (!sectionRepository.existsById(sectionId)) {
            throw new ResourceNotFoundException("Section not found with id: " + sectionId);
        }
        return timetableRepository.findBySectionId(sectionId).stream()
                .map(TimetableSlotDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TimetableSlotDto> getTimetableForTeacher(Long teacherId) {
        if (!userRepository.existsById(teacherId)) {
            throw new ResourceNotFoundException("Teacher not found with id: " + teacherId);
        }
        return timetableRepository.findByTeacherId(teacherId).stream()
                .map(TimetableSlotDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TimetableSlotDto> getMyTimetable(UserPrincipal principal) {
        if (principal == null) {
            return Collections.emptyList();
        }

        boolean isStudent = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().contains("STUDENT"));
        if (isStudent) {
            Optional<Student> studentOpt = studentRepository.findByUserId(principal.getId());
            if (studentOpt.isPresent()) {
                List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentOpt.get().getId());
                if (!enrollments.isEmpty()) {
                    return getTimetableForSection(enrollments.get(0).getSection().getId());
                }
            }
            return Collections.emptyList();
        }

        boolean isTeacher = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().contains("TEACHER"));
        if (isTeacher) {
            return timetableRepository.findByTeacherId(principal.getId()).stream()
                    .map(TimetableSlotDto::fromEntity)
                    .collect(Collectors.toList());
        }

        boolean isParent = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().contains("PARENT"));
        if (isParent) {
            List<StudentDto> children = guardianService.getMyChildren(principal);
            if (!children.isEmpty()) {
                List<Enrollment> enrollments = enrollmentRepository.findByStudentId(children.get(0).getId());
                if (!enrollments.isEmpty()) {
                    return getTimetableForSection(enrollments.get(0).getSection().getId());
                }
            }
            return Collections.emptyList();
        }

        // Admin default: section 1 if exists, else empty
        return timetableRepository.findBySectionId(1L).stream()
                .map(TimetableSlotDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteSlot(Long slotId) {
        if (!timetableRepository.existsById(slotId)) {
            throw new ResourceNotFoundException("Timetable slot not found with id: " + slotId);
        }
        timetableRepository.deleteById(slotId);
    }
}
