package com.bluebells.staff;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherAssignmentRepository extends JpaRepository<TeacherAssignment, Long> {
    List<TeacherAssignment> findByTeacherIdAndAcademicSessionId(Long teacherId, Long sessionId);
    List<TeacherAssignment> findBySectionIdAndAcademicSessionId(Long sectionId, Long sessionId);
    List<TeacherAssignment> findByAcademicSessionId(Long sessionId);
    boolean existsByTeacherIdAndSectionIdAndSubjectIdAndAcademicSessionId(Long teacherId, Long sectionId, Long subjectId, Long sessionId);
}
