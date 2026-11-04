package com.bluebells.school;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolProfileRepository extends JpaRepository<SchoolProfile, Long> {
}
