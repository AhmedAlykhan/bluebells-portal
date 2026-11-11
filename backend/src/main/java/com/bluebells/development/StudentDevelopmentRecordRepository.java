package com.bluebells.development;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentDevelopmentRecordRepository extends JpaRepository<StudentDevelopmentRecord, Long> {

    List<StudentDevelopmentRecord> findByStudentIdOrderByAchievementDateDesc(Long studentId);

    List<StudentDevelopmentRecord> findByStudentIdAndAcademicSessionIdOrderByAchievementDateDesc(Long studentId, Long sessionId);

    List<StudentDevelopmentRecord> findByCategory(String category);

    long countByStudentId(Long studentId);
}
