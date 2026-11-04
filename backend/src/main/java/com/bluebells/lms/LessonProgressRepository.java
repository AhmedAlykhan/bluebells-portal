package com.bluebells.lms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    Optional<LessonProgress> findByStudentIdAndLessonId(Long studentId, Long lessonId);
    List<LessonProgress> findByStudentIdAndLessonCourseId(Long studentId, Long courseId);
    long countByStudentIdAndLessonCourseIdAndCompletedTrue(Long studentId, Long courseId);
}
