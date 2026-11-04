package com.bluebells.examination;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarksEntryRepository extends JpaRepository<MarksEntry, Long> {

    List<MarksEntry> findByExamId(Long examId);

    List<MarksEntry> findByExamIdAndSubjectId(Long examId, Long subjectId);

    List<MarksEntry> findByExamIdAndStudentId(Long examId, Long studentId);

    Optional<MarksEntry> findByExamIdAndSubjectIdAndStudentId(Long examId, Long subjectId, Long studentId);

    boolean existsByExamIdAndSubjectIdAndStudentId(Long examId, Long subjectId, Long studentId);
}
