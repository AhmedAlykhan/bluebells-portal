package com.bluebells.integrations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchoolIntegrationRepository extends JpaRepository<SchoolIntegration, Long> {

    Optional<SchoolIntegration> findByIntegrationKey(String integrationKey);

    boolean existsByIntegrationKey(String integrationKey);
}
