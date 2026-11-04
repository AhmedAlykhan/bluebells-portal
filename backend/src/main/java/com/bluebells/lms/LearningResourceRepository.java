package com.bluebells.lms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningResourceRepository extends JpaRepository<LearningResource, Long> {
    List<LearningResource> findByCourseId(Long courseId);
    List<LearningResource> findByModuleId(Long moduleId);
    List<LearningResource> findByLessonId(Long lessonId);
}
