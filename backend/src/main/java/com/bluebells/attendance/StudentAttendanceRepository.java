package com.bluebells.attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentAttendanceRepository extends JpaRepository<StudentAttendance, Long> {

    Optional<StudentAttendance> findByEnrollmentIdAndAttendanceDate(Long enrollmentId, LocalDate attendanceDate);

    boolean existsByEnrollmentIdAndAttendanceDate(Long enrollmentId, LocalDate attendanceDate);

    @Query("SELECT a FROM StudentAttendance a WHERE a.enrollment.section.id = :sectionId AND a.attendanceDate = :date")
    List<StudentAttendance> findBySectionIdAndAttendanceDate(Long sectionId, LocalDate date);

    @Query("SELECT a FROM StudentAttendance a WHERE a.enrollment.student.id = :studentId " +
           "AND a.attendanceDate BETWEEN :startDate AND :endDate ORDER BY a.attendanceDate ASC")
    List<StudentAttendance> findByStudentIdAndDateRange(Long studentId, LocalDate startDate, LocalDate endDate);

    List<StudentAttendance> findByAttendanceDate(LocalDate attendanceDate);
}
