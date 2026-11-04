package com.bluebells.lms;

import com.bluebells.academics.Section;
import com.bluebells.academics.SectionRepository;
import com.bluebells.academics.Subject;
import com.bluebells.academics.SubjectRepository;
import com.bluebells.exception.ApiException;
import com.bluebells.exception.ResourceNotFoundException;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
import com.bluebells.lms.dto.*;
import com.bluebells.security.UserPrincipal;
import com.bluebells.students.Student;
import com.bluebells.students.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LmsService {

    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final SubjectRepository subjectRepository;
    private final SectionRepository sectionRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public LmsService(
            CourseRepository courseRepository,
            LessonRepository lessonRepository,
            AssignmentRepository assignmentRepository,
            AssignmentSubmissionRepository submissionRepository,
            SubjectRepository subjectRepository,
            SectionRepository sectionRepository,
            UserRepository userRepository,
            StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.subjectRepository = subjectRepository;
        this.sectionRepository = sectionRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
    }

    // --- Courses ---

    @Transactional
    public CourseDto createCourse(CreateCourseRequest request) {
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + request.getSubjectId()));

        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + request.getSectionId()));

        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + request.getTeacherId()));

        Course course = new Course(subject, section, teacher, request.getTitle(), request.getDescription());
        Course saved = courseRepository.save(course);
        return CourseDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public CourseDto getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return CourseDto.fromEntity(course);
    }

    @Transactional(readOnly = true)
    public List<CourseDto> getCoursesForSection(Long sectionId) {
        if (!sectionRepository.existsById(sectionId)) {
            throw new ResourceNotFoundException("Section not found with id: " + sectionId);
        }
        return courseRepository.findBySectionId(sectionId).stream()
                .map(CourseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // --- Lessons ---

    @Transactional
    public LessonDto createLesson(Long courseId, CreateLessonRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        Lesson lesson = new Lesson(course, request.getTitle(), request.getContent(), request.getSequenceOrder());
        Lesson saved = lessonRepository.save(lesson);
        return LessonDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<LessonDto> getLessonsForCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found with id: " + courseId);
        }
        return lessonRepository.findByCourseIdOrderBySequenceOrderAsc(courseId).stream()
                .map(LessonDto::fromEntity)
                .collect(Collectors.toList());
    }

    // --- Assignments ---

    @Transactional
    public AssignmentDto createAssignment(Long courseId, CreateAssignmentRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        Assignment assignment = new Assignment(
                course,
                request.getTitle(),
                request.getInstructions(),
                request.getMaxMarks(),
                request.getDueDate(),
                request.isAllowLateSubmission()
        );
        Assignment saved = assignmentRepository.save(assignment);
        return AssignmentDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<AssignmentDto> getAssignmentsForCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found with id: " + courseId);
        }
        return assignmentRepository.findByCourseId(courseId).stream()
                .map(AssignmentDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AssignmentDto getAssignmentById(Long id) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + id));
        return AssignmentDto.fromEntity(assignment);
    }

    // --- Submissions & Grading ---

    @Transactional
    public AssignmentSubmissionDto submitAssignment(Long assignmentId, SubmitAssignmentRequest request, UserPrincipal currentUser) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + assignmentId));

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.getStudentId()));

        Instant now = Instant.now();
        boolean isLate = now.isAfter(assignment.getDueDate());

        if (isLate && !assignment.isAllowLateSubmission()) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Late submissions are not accepted for this assignment (Due date: " + assignment.getDueDate() + ")");
        }

        Optional<AssignmentSubmission> existingOpt = submissionRepository.findByAssignmentIdAndStudentId(assignmentId, student.getId());
        AssignmentSubmission submission;
        if (existingOpt.isPresent()) {
            submission = existingOpt.get();
            submission.setSubmissionText(request.getSubmissionText());
            submission.setFileUrl(request.getFileUrl());
            submission.setSubmittedAt(now);
            submission.setLate(isLate);
            submission.setStatus("RESUBMITTED");
        } else {
            submission = new AssignmentSubmission(assignment, student, request.getSubmissionText(), request.getFileUrl(), isLate);
        }

        AssignmentSubmission saved = submissionRepository.save(submission);
        return AssignmentSubmissionDto.fromEntity(saved);
    }

    @Transactional
    public AssignmentSubmissionDto gradeSubmission(Long submissionId, GradeSubmissionRequest request, UserPrincipal currentUser) {
        AssignmentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment submission not found with id: " + submissionId));

        if (request.getMarksObtained().compareTo(submission.getAssignment().getMaxMarks()) > 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    String.format("Marks obtained (%s) cannot exceed maximum marks (%s)",
                            request.getMarksObtained(), submission.getAssignment().getMaxMarks()));
        }

        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().contains("ADMIN") || a.getAuthority().contains("PRINCIPAL"));
        if (!isAdmin) {
            User courseTeacher = submission.getAssignment().getCourse().getTeacher();
            if (courseTeacher != null && !courseTeacher.getId().equals(currentUser.getId())) {
                throw new com.bluebells.exception.ForbiddenException("Access denied: You are not authorized to grade assignments for this course");
            }
        }

        User teacher = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUser.getId()));

        submission.setMarksObtained(request.getMarksObtained());
        submission.setTeacherFeedback(request.getTeacherFeedback());
        submission.setStatus("GRADED");
        submission.setGradedAt(Instant.now());
        submission.setGradedBy(teacher);

        AssignmentSubmission saved = submissionRepository.save(submission);
        return AssignmentSubmissionDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<AssignmentSubmissionDto> getSubmissionsForAssignment(Long assignmentId) {
        if (!assignmentRepository.existsById(assignmentId)) {
            throw new ResourceNotFoundException("Assignment not found with id: " + assignmentId);
        }
        return submissionRepository.findByAssignmentId(assignmentId).stream()
                .map(AssignmentSubmissionDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AssignmentSubmissionDto getStudentSubmission(Long assignmentId, Long studentId) {
        AssignmentSubmission submission = submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Submission not found for assignment %d and student %d", assignmentId, studentId)));
        return AssignmentSubmissionDto.fromEntity(submission);
    }
}
