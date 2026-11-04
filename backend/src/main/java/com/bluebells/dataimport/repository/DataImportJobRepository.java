package com.bluebells.dataimport.repository;

import com.bluebells.dataimport.entity.DataImportJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataImportJobRepository extends JpaRepository<DataImportJob, Long> {

    Optional<DataImportJob> findByJobId(String jobId);

    List<DataImportJob> findAllByOrderByCreatedAtDesc();

    Page<DataImportJob> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT COUNT(j) FROM DataImportJob j WHERE j.status = 'COMPLETED' OR j.status = 'COMPLETED_WITH_ERRORS'")
    long countCompletedJobs();

    @Query("SELECT COALESCE(SUM(j.importedRows), 0) FROM DataImportJob j")
    long sumImportedRows();

    @Query("SELECT COALESCE(SUM(j.invalidRows), 0) FROM DataImportJob j")
    long sumFailedRows();

    @Query("SELECT COALESCE(SUM(j.duplicateRows), 0) FROM DataImportJob j")
    long sumDuplicateRows();

    @Query("SELECT COUNT(j) FROM DataImportJob j WHERE j.status IN ('UPLOADED', 'VALIDATED', 'PROCESSING')")
    long countPendingJobs();

    Optional<DataImportJob> findTopByOrderByCreatedAtDesc();
}
