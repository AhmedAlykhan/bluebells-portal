package com.bluebells.academics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Long> {
    List<ClassEntity> findByGradeLevelId(Long gradeLevelId);
    Optional<ClassEntity> findByCode(String code);
    Optional<ClassEntity> findByName(String name);
    boolean existsByCode(String code);
}
