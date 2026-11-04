package com.bluebells.academics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AcademicSessionRepository extends JpaRepository<AcademicSession, Long> {
    Optional<AcademicSession> findByName(String name);
    Optional<AcademicSession> findByCurrentTrue();
    default Optional<AcademicSession> findByIsCurrentTrue() {
        return findByCurrentTrue();
    }
    boolean existsByName(String name);

    @Modifying
    @Query("UPDATE AcademicSession s SET s.current = false WHERE s.id != :sessionId")
    void resetOtherSessionsCurrentStatus(Long sessionId);
}
