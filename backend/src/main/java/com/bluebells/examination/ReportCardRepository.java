package com.bluebells.examination;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportCardRepository extends JpaRepository<ReportCard, Long> {

    List<ReportCard> findByExamId(Long examId);

    Optional<ReportCard> findByExamIdAndStudentId(Long examId, Long studentId);

    List<ReportCard> findByStudentId(Long studentId);
}
