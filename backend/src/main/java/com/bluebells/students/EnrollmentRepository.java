package com.bluebells.students;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByStudentIdAndAcademicSessionId(Long studentId, Long sessionId);
    List<Enrollment> findBySectionIdAndAcademicSessionId(Long sectionId, Long sessionId);
    List<Enrollment> findByStudentId(Long studentId);
    boolean existsByStudentIdAndAcademicSessionId(Long studentId, Long sessionId);
}
