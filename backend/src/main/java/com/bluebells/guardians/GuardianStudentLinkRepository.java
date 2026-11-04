package com.bluebells.guardians;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuardianStudentLinkRepository extends JpaRepository<GuardianStudentLink, GuardianStudentLinkId> {
    List<GuardianStudentLink> findByGuardianId(Long guardianId);
    List<GuardianStudentLink> findByStudentId(Long studentId);

    @Query("SELECT gsl FROM GuardianStudentLink gsl WHERE gsl.guardian.user.id = :userId")
    List<GuardianStudentLink> findByGuardianUserId(Long userId);

    @Query("SELECT COUNT(gsl) > 0 FROM GuardianStudentLink gsl WHERE gsl.guardian.user.id = :userId AND gsl.student.id = :studentId")
    boolean existsByGuardianUserIdAndStudentId(Long userId, Long studentId);
}
