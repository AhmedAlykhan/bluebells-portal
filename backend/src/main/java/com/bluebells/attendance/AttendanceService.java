package com.bluebells.attendance;

import com.bluebells.attendance.dto.AttendanceCorrectionRequest;
import com.bluebells.attendance.dto.AttendanceItemRequest;
import com.bluebells.attendance.dto.AttendanceRecordDto;
import com.bluebells.attendance.dto.AttendanceSummaryDto;
import com.bluebells.attendance.dto.RecordSectionAttendanceRequest;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    private final StudentAttendanceRepository attendanceRepository;
    private final AttendanceCorrectionRepository correctionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public AttendanceService(
            StudentAttendanceRepository attendanceRepository,
            AttendanceCorrectionRepository correctionRepository,
            EnrollmentRepository enrollmentRepository,
            UserRepository userRepository,
            StudentRepository studentRepository) {
        this.attendanceRepository = attendanceRepository;
        this.correctionRepository = correctionRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public List<AttendanceRecordDto> recordSectionAttendance(RecordSectionAttendanceRequest request, UserPrincipal currentUser) {
        User recorder = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUser.getId()));

        LocalDate date = request.getAttendanceDate();
        List<StudentAttendance> toSave = new ArrayList<>();

        for (AttendanceItemRequest item : request.getItems()) {
            if (attendanceRepository.existsByEnrollmentIdAndAttendanceDate(item.getEnrollmentId(), date)) {
                throw new ApiException(HttpStatus.CONFLICT,
                        String.format("Attendance already recorded for enrollment ID %d on date %s. Use correction request to update.",
                                item.getEnrollmentId(), date));
            }

            Enrollment enrollment = enrollmentRepository.findById(item.getEnrollmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + item.getEnrollmentId()));

            StudentAttendance record = new StudentAttendance(
                    enrollment,
                    date,
                    item.getStatus().toUpperCase(),
                    item.getRemarks(),
                    recorder
            );
            toSave.add(record);
        }

        List<StudentAttendance> saved = attendanceRepository.saveAll(toSave);
        return saved.stream().map(AttendanceRecordDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecordDto> getSectionAttendance(Long sectionId, LocalDate date) {
        List<StudentAttendance> records = attendanceRepository.findBySectionIdAndAttendanceDate(sectionId, date);
        return records.stream().map(AttendanceRecordDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecordDto> getStudentAttendanceHistory(Long studentId, LocalDate startDate, LocalDate endDate) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }
        List<StudentAttendance> records = attendanceRepository.findByStudentIdAndDateRange(studentId, startDate, endDate);
        return records.stream().map(AttendanceRecordDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AttendanceSummaryDto getStudentAttendanceSummary(Long studentId, LocalDate startDate, LocalDate endDate) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        List<StudentAttendance> records = attendanceRepository.findByStudentIdAndDateRange(studentId, startDate, endDate);
        int totalDays = records.size();
        int presentDays = 0;
        int absentDays = 0;
        int lateDays = 0;
        int excusedDays = 0;

        for (StudentAttendance r : records) {
            String s = r.getStatus().toUpperCase();
            switch (s) {
                case "PRESENT" -> presentDays++;
                case "ABSENT" -> absentDays++;
                case "LATE" -> lateDays++;
                case "EXCUSED" -> excusedDays++;
            }
        }

        double percentage = 0.0;
        if (totalDays > 0) {
            // Count present and excused as attendance, or pure present
            BigDecimal presentDecimal = BigDecimal.valueOf(presentDays);
            BigDecimal totalDecimal = BigDecimal.valueOf(totalDays);
            percentage = presentDecimal.divide(totalDecimal, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        return new AttendanceSummaryDto(
                student.getId(),
                student.getFullName(),
                totalDays,
                presentDays,
                absentDays,
                lateDays,
                excusedDays,
                percentage
        );
    }

    @Transactional
    public AttendanceRecordDto correctAttendance(Long attendanceId, AttendanceCorrectionRequest request, UserPrincipal currentUser) {
        StudentAttendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with id: " + attendanceId));

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUser.getId()));

        String oldStatus = attendance.getStatus();
        String newStatus = request.getNewStatus().toUpperCase();

        AttendanceCorrection correction = new AttendanceCorrection(
                attendance,
                oldStatus,
                newStatus,
                request.getReason(),
                user,
                user
        );
        correctionRepository.save(correction);

        attendance.setStatus(newStatus);
        StudentAttendance updated = attendanceRepository.save(attendance);

        return AttendanceRecordDto.fromEntity(updated);
    }
}
