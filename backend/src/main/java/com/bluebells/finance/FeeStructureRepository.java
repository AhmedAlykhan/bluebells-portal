package com.bluebells.finance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {
    List<FeeStructure> findByAcademicSessionIdAndClassEntityId(Long sessionId, Long classId);
    List<FeeStructure> findByAcademicSessionId(Long sessionId);
    Optional<FeeStructure> findByAcademicSessionIdAndClassEntityIdAndCategoryId(Long sessionId, Long classId, Long categoryId);
}
