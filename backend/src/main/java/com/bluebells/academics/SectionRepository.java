package com.bluebells.academics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByClassEntityId(Long classId);
    Optional<Section> findByClassEntityIdAndName(Long classId, String name);
    boolean existsByClassEntityIdAndName(Long classId, String name);
}
