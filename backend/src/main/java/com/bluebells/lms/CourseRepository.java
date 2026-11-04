package com.bluebells.lms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findBySectionId(Long sectionId);
    List<Course> findByTeacherId(Long teacherId);
    boolean existsBySectionIdAndSubjectId(Long sectionId, Long subjectId);
}
