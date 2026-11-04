package com.bluebells.students;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByAdmissionNumber(String admissionNumber);
    Optional<Student> findByUserId(Long userId);
    boolean existsByAdmissionNumber(String admissionNumber);

    @Override
    @EntityGraph(attributePaths = {"enrollments", "enrollments.section", "enrollments.section.classEntity"})
    Page<Student> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"enrollments", "enrollments.section", "enrollments.section.classEntity"})
    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.admissionNumber) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Student> searchStudents(String query, Pageable pageable);
}
