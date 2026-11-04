package com.bluebells.academics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByClassEntityId(Long classId);
    Optional<Subject> findByClassEntityIdAndCode(Long classId, String code);
    boolean existsByClassEntityIdAndCode(Long classId, String code);
}
