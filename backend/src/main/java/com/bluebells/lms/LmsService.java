package com.bluebells.lms;

import com.bluebells.academics.Section;
import com.bluebells.academics.SectionRepository;
import com.bluebells.academics.Subject;
import com.bluebells.academics.SubjectRepository;
import com.bluebells.exception.ApiException;
import com.bluebells.exception.ForbiddenException;
import com.bluebells.exception.ResourceNotFoundException;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
import com.bluebells.lms.dto.*;
import com.bluebells.notifications.NotificationService;
import com.bluebells.notifications.dto.SendNotificationRequest;
import com.bluebells.security.UserPrincipal;
import com.bluebells.students.Enrollment;
import com.bluebells.students.EnrollmentRepository;
import com.bluebells.students.Student;
import com.bluebells.students.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class LmsService {

    private static final Logger log = LoggerFactory.getLogger(LmsService.class);

    private final CourseRepository courseRepository;
    private final CourseModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final LearningResourceRepository resourceRepository;
    private final YouTubeVideoRepository videoRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final SubmissionFileRepository submissionFileRepository;
    private final LessonProgressRepository progressRepository;
    private final SubjectRepository subjectRepository;
    private final SectionRepository sectionRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final FileStorageService fileStorageService;
    private final NotificationService notificationService;

    private static final Pattern YOUTUBE_PATTERN = Pattern.compile(
            "(?:https?:\\/\\/)?(?:www\\.)?(?:youtube\\.com\\/(?:watch\\?v=|embed\\/|v\\/|shorts\\/)|youtu\\.be\\/)([a-zA-Z0-9_-]{11})"
    );

    public LmsService(
            CourseRepository courseRepository,
            CourseModuleRepository moduleRepository,
            LessonRepository lessonRepository,
            LearningResourceRepository resourceRepository,
            YouTubeVideoRepository videoRepository,
            AssignmentRepository assignmentRepository,
            AssignmentSubmissionRepository submissionRepository,
            SubmissionFileRepository submissionFileRepository,
            LessonProgressRepository progressRepository,
            SubjectRepository subjectRepository,
            SectionRepository sectionRepository,
            UserRepository userRepository,
            StudentRepository studentRepository,
            EnrollmentRepository enrollmentRepository,
            FileStorageService fileStorageService,
            NotificationService notificationService) {
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
        this.resourceRepository = resourceRepository;
        this.videoRepository = videoRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.submissionFileRepository = submissionFileRepository;
        this.progressRepository = progressRepository;
        this.subjectRepository = subjectRepository;
        this.sectionRepository = sectionRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.fileStorageService = fileStorageService;
        this.notificationService = notificationService;
    }

    // =========================================================================
    // Courses
    // =========================================================================

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

    @Transactional(readOnly = true)
    public List<CourseDto> getCoursesForTeacher(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId).stream()
                .map(CourseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseDto> getCoursesForStudent(Long studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        Set<Long> sectionIds = enrollments.stream()
                .filter(e -> e.getSection() != null)
                .map(e -> e.getSection().getId())
                .collect(Collectors.toSet());

        List<CourseDto> courses = new ArrayList<>();
        for (Long secId : sectionIds) {
            courses.addAll(courseRepository.findBySectionId(secId).stream()
                    .map(c -> {
                        CourseDto dto = CourseDto.fromEntity(c);
                        enrichCourseDtoWithStudentProgress(dto, c, studentId);
                        return dto;
                    })
                    .collect(Collectors.toList()));
        }
        return courses;
    }

    @Transactional(readOnly = true)
    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(CourseDto::fromEntity)
                .collect(Collectors.toList());
    }

    private void enrichCourseDtoWithStudentProgress(CourseDto dto, Course course, Long studentId) {
        if (course.getLessons() != null && !course.getLessons().isEmpty()) {
            long completedCount = progressRepository.countByStudentIdAndLessonCourseIdAndCompletedTrue(studentId, course.getId());
            double pct = ((double) completedCount / course.getLessons().size()) * 100.0;
            dto.setCompletionPercentage(Math.round(pct * 10.0) / 10.0);
        } else {
            dto.setCompletionPercentage(0.0);
        }
    }

    // =========================================================================
    // Modules
    // =========================================================================

    @Transactional
    public CourseModuleDto createModule(Long courseId, CreateModuleRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        CourseModule module = new CourseModule(course, request.getTitle(), request.getDescription(), request.getDisplayOrder());
        CourseModule saved = moduleRepository.save(module);
        return CourseModuleDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<CourseModuleDto> getModulesForCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found with id: " + courseId);
        }
        return moduleRepository.findByCourseIdOrderByDisplayOrderAsc(courseId).stream()
                .map(CourseModuleDto::fromEntity)
                .collect(Collectors.toList());
    }

    // =========================================================================
    // Lessons
    // =========================================================================

    @Transactional
    public LessonDto createLesson(Long courseId, CreateLessonRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        CourseModule module = null;
        if (request.getModuleId() != null) {
            module = moduleRepository.findById(request.getModuleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + request.getModuleId()));
        }

        Lesson lesson = new Lesson(
                course,
                module,
                request.getTitle(),
                request.getContent(),
                request.getSequenceOrder(),
                request.getLessonType() != null ? request.getLessonType() : "LECTURE",
                request.getDurationMinutes()
        );
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

    @Transactional(readOnly = true)
    public List<LessonDto> getLessonsForCourseWithStudentStatus(Long courseId, Long studentId) {
        List<LessonDto> dtos = getLessonsForCourse(courseId);
        if (studentId != null) {
            Map<Long, Boolean> progressMap = progressRepository.findByStudentIdAndLessonCourseId(studentId, courseId).stream()
                    .collect(Collectors.toMap(p -> p.getLesson().getId(), LessonProgress::isCompleted, (a, b) -> a));
            dtos.forEach(dto -> dto.setCompleted(progressMap.getOrDefault(dto.getId(), false)));
        }
        return dtos;
    }

    @Transactional
    public LessonProgressDto markLessonProgress(Long lessonId, Long studentId, boolean completed) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        LessonProgress progress = progressRepository.findByStudentIdAndLessonId(studentId, lessonId)
                .orElseGet(() -> new LessonProgress(student, lesson, completed));

        progress.setCompleted(completed);
        progress.setLastAccessedAt(Instant.now());
        LessonProgress saved = progressRepository.save(progress);

        return LessonProgressDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getStudentProgressForCourse(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        List<Lesson> lessons = lessonRepository.findByCourseIdOrderBySequenceOrderAsc(courseId);
        long completedCount = progressRepository.countByStudentIdAndLessonCourseIdAndCompletedTrue(studentId, courseId);
        double percentage = lessons.isEmpty() ? 0.0 : ((double) completedCount / lessons.size()) * 100.0;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("courseId", courseId);
        result.put("courseTitle", course.getTitle());
        result.put("studentId", studentId);
        result.put("totalLessons", lessons.size());
        result.put("completedLessons", completedCount);
        result.put("completionPercentage", Math.round(percentage * 10.0) / 10.0);
        return result;
    }

    // =========================================================================
    // Learning Resources
    // =========================================================================

    @Transactional
    public LearningResourceDto createResource(Long courseId, CreateResourceRequest request, UserPrincipal currentUser) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        CourseModule module = null;
        if (request.getModuleId() != null) {
            module = moduleRepository.findById(request.getModuleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + request.getModuleId()));
        }

        Lesson lesson = null;
        if (request.getLessonId() != null) {
            lesson = lessonRepository.findById(request.getLessonId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + request.getLessonId()));
        }

        User uploader = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUser.getId()));

        LearningResource resource = new LearningResource(
                course,
                module,
                lesson,
                request.getTitle(),
                request.getDescription(),
                request.getResourceType() != null ? request.getResourceType() : "PDF",
                request.getFilePath(),
                request.getOriginalFilename(),
                request.getFileSizeBytes(),
                request.getContentType(),
                request.getExternalUrl(),
                request.isDownloadable(),
                uploader
        );

        LearningResource saved = resourceRepository.save(resource);
        return LearningResourceDto.fromEntity(saved);
    }

    @Transactional
    public LearningResourceDto uploadResourceFile(Long courseId, Long moduleId, Long lessonId,
                                                  String title, String description,
                                                  MultipartFile file, UserPrincipal currentUser) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        CourseModule module = null;
        if (moduleId != null) {
            module = moduleRepository.findById(moduleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + moduleId));
        }

        Lesson lesson = null;
        if (lessonId != null) {
            lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));
        }

        User uploader = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUser.getId()));

        FileStorageService.StoredFile stored = fileStorageService.storeFile(file, "resources");

        LearningResource resource = new LearningResource(
                course,
                module,
                lesson,
                title != null && !title.isBlank() ? title : file.getOriginalFilename(),
                description,
                stored.getFileType(),
                stored.getRelativePath(),
                stored.getOriginalFilename(),
                stored.getSize(),
                stored.getContentType(),
                null,
                true,
                uploader
        );

        LearningResource saved = resourceRepository.save(resource);
        return LearningResourceDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<LearningResourceDto> getResourcesForCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found with id: " + courseId);
        }
        return resourceRepository.findByCourseId(courseId).stream()
                .map(LearningResourceDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LearningResourceDto getResourceById(Long resourceId) {
        LearningResource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Learning resource not found with id: " + resourceId));
        return LearningResourceDto.fromEntity(resource);
    }

    // =========================================================================
    // YouTube Video Integration
    // =========================================================================

    @Transactional
    public YouTubeVideoDto addYouTubeVideo(Long courseId, CreateYouTubeVideoRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        CourseModule module = null;
        if (request.getModuleId() != null) {
            module = moduleRepository.findById(request.getModuleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + request.getModuleId()));
        }

        Lesson lesson = null;
        if (request.getLessonId() != null) {
            lesson = lessonRepository.findById(request.getLessonId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + request.getLessonId()));
        }

        String videoId = extractYouTubeVideoId(request.getYoutubeUrl());
        if (videoId == null || videoId.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid YouTube URL: Could not extract 11-character video ID from: " + request.getYoutubeUrl());
        }

        YouTubeVideo video = new YouTubeVideo(
                course,
                module,
                lesson,
                request.getTitle(),
                request.getDescription(),
                request.getYoutubeUrl(),
                videoId,
                request.getDurationSeconds(),
                request.getDisplayOrder()
        );

        YouTubeVideo saved = videoRepository.save(video);
        return YouTubeVideoDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<YouTubeVideoDto> getVideosForCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found with id: " + courseId);
        }
        return videoRepository.findByCourseIdOrderByDisplayOrderAsc(courseId).stream()
                .map(YouTubeVideoDto::fromEntity)
                .collect(Collectors.toList());
    }

    public String extractYouTubeVideoId(String url) {
        if (url == null || url.isBlank()) return null;
        Matcher matcher = YOUTUBE_PATTERN.matcher(url.trim());
        if (matcher.find()) {
            return matcher.group(1);
        }
        // Fallback check if user entered direct 11-char ID
        if (url.trim().matches("^[a-zA-Z0-9_-]{11}$")) {
            return url.trim();
        }
        return null;
    }

    // =========================================================================
    // Assignments
    // =========================================================================

    @Transactional
    public AssignmentDto createAssignment(Long courseId, CreateAssignmentRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        CourseModule module = null;
        if (request.getModuleId() != null) {
            module = moduleRepository.findById(request.getModuleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + request.getModuleId()));
        }

        Assignment assignment = new Assignment(
                course,
                module,
                request.getTitle(),
                request.getInstructions(),
                request.getMaxMarks(),
                request.getPassingMarks() != null ? request.getPassingMarks() : new BigDecimal("40.00"),
                request.getDueDate(),
                request.isAllowLateSubmission(),
                request.getAllowedFormats() != null ? request.getAllowedFormats() : "PDF,IMAGES,DOCX",
                request.getAttachmentUrl(),
                request.getAttachmentName(),
                request.getAttachmentSizeBytes()
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
    public List<AssignmentDto> getAssignmentsForCourseWithStudentSubmission(Long courseId, Long studentId) {
        List<AssignmentDto> assignments = getAssignmentsForCourse(courseId);
        if (studentId != null) {
            for (AssignmentDto dto : assignments) {
                submissionRepository.findByAssignmentIdAndStudentId(dto.getId(), studentId)
                        .ifPresent(s -> dto.setStudentSubmission(AssignmentSubmissionDto.fromEntity(s)));
            }
        }
        return assignments;
    }

    @Transactional(readOnly = true)
    public AssignmentDto getAssignmentById(Long id) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + id));
        return AssignmentDto.fromEntity(assignment);
    }

    // =========================================================================
    // Submissions & Multi-Photo Uploads
    // =========================================================================

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
            if (request.getFileUrl() != null && !request.getFileUrl().isBlank()) {
                submission.setFileUrl(request.getFileUrl());
            }
            submission.setSubmittedAt(now);
            submission.setLate(isLate);
            submission.setStatus("RESUBMITTED");
        } else {
            submission = new AssignmentSubmission(assignment, student, request.getSubmissionText(), request.getFileUrl(), isLate);
        }

        AssignmentSubmission saved = submissionRepository.save(submission);

        // Process any multiple file URLs passed in the request
        if (request.getFileUrls() != null && !request.getFileUrls().isEmpty()) {
            for (int i = 0; i < request.getFileUrls().size(); i++) {
                String url = request.getFileUrls().get(i);
                String name = (request.getFileNames() != null && request.getFileNames().size() > i) ? request.getFileNames().get(i) : "Submission_File_" + (i + 1);
                Long size = (request.getFileSizes() != null && request.getFileSizes().size() > i) ? request.getFileSizes().get(i) : 100000L;
                String type = (request.getFileTypes() != null && request.getFileTypes().size() > i) ? request.getFileTypes().get(i) : "IMAGE";

                SubmissionFile subFile = new SubmissionFile(saved, url, name, size, type, i + 1);
                submissionFileRepository.save(subFile);
            }
        }

        // Notify teacher of new/resubmitted assignment
        try {
            User teacher = assignment.getCourse().getTeacher();
            if (teacher != null) {
                notificationService.sendNotification(new SendNotificationRequest(
                        teacher.getId(),
                        null,
                        "Assignment Submission: " + assignment.getTitle(),
                        String.format("Student %s has submitted work for '%s'.", student.getFullName(), assignment.getTitle()),
                        "LMS"
                ));
            }
        } catch (Exception e) {
            log.warn("Could not dispatch submission notification: {}", e.getMessage());
        }

        return AssignmentSubmissionDto.fromEntity(saved);
    }

    @Transactional
    public AssignmentSubmissionDto submitAssignmentWithFiles(Long assignmentId, Long studentId,
                                                             String submissionText,
                                                             List<MultipartFile> files,
                                                             UserPrincipal currentUser) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + assignmentId));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

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
            submission.setSubmissionText(submissionText);
            submission.setSubmittedAt(now);
            submission.setLate(isLate);
            submission.setStatus("RESUBMITTED");
        } else {
            submission = new AssignmentSubmission(assignment, student, submissionText, null, isLate);
        }

        AssignmentSubmission saved = submissionRepository.save(submission);

        if (files != null && !files.isEmpty()) {
            int order = 1;
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;
                FileStorageService.StoredFile stored = fileStorageService.storeFile(file, "submissions");
                SubmissionFile subFile = new SubmissionFile(
                        saved,
                        stored.getRelativePath(),
                        stored.getOriginalFilename(),
                        stored.getSize(),
                        stored.getFileType(),
                        order++
                );
                submissionFileRepository.save(subFile);
                if (saved.getFileUrl() == null) {
                    saved.setFileUrl(stored.getRelativePath());
                }
            }
        }

        AssignmentSubmission reSaved = submissionRepository.save(saved);
        return AssignmentSubmissionDto.fromEntity(reSaved);
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
                throw new ForbiddenException("Access denied: You are not authorized to grade assignments for this course");
            }
        }

        User teacher = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUser.getId()));

        String newStatus = request.getStatus() != null && !request.getStatus().isBlank()
                ? request.getStatus().toUpperCase()
                : "GRADED";

        submission.setMarksObtained(request.getMarksObtained());
        submission.setTeacherFeedback(request.getTeacherFeedback());
        submission.setStatus(newStatus);
        submission.setGradedAt(Instant.now());
        submission.setGradedBy(teacher);

        AssignmentSubmission saved = submissionRepository.save(submission);

        // Notify student of grading / return for correction
        try {
            if (submission.getStudent() != null && submission.getStudent().getUser() != null) {
                boolean isCorrection = "RETURNED_FOR_CORRECTION".equalsIgnoreCase(newStatus);
                String title = isCorrection
                        ? "Assignment Returned for Correction: " + submission.getAssignment().getTitle()
                        : "Assignment Graded: " + submission.getAssignment().getTitle();
                String msg = isCorrection
                        ? String.format("Teacher %s returned your assignment for correction. Feedback: %s", teacher.getFullName(), request.getTeacherFeedback())
                        : String.format("Your assignment '%s' was graded: %s / %s. Feedback: %s",
                        submission.getAssignment().getTitle(), request.getMarksObtained(), submission.getAssignment().getMaxMarks(), request.getTeacherFeedback());

                notificationService.sendNotification(new SendNotificationRequest(
                        submission.getStudent().getUser().getId(),
                        null,
                        title,
                        msg,
                        "LMS"
                ));
            }
        } catch (Exception e) {
            log.warn("Could not dispatch grade notification: {}", e.getMessage());
        }

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
