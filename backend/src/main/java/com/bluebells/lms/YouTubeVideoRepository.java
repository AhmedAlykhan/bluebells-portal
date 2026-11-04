package com.bluebells.lms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YouTubeVideoRepository extends JpaRepository<YouTubeVideo, Long> {
    List<YouTubeVideo> findByCourseIdOrderByDisplayOrderAsc(Long courseId);
    List<YouTubeVideo> findByModuleIdOrderByDisplayOrderAsc(Long moduleId);
    List<YouTubeVideo> findByLessonIdOrderByDisplayOrderAsc(Long lessonId);
}
