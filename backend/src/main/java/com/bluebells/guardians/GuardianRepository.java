package com.bluebells.guardians;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuardianRepository extends JpaRepository<Guardian, Long> {
    Optional<Guardian> findByUserId(Long userId);
    Optional<Guardian> findByPhonePrimary(String phonePrimary);
    Optional<Guardian> findByCnicNumber(String cnicNumber);
}
