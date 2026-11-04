package com.bluebells.dataimport.repository;

import com.bluebells.dataimport.entity.DataImportJobRow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataImportJobRowRepository extends JpaRepository<DataImportJobRow, Long> {

    List<DataImportJobRow> findByJobIdOrderByRowNumberAsc(Long jobId);

    Page<DataImportJobRow> findByJobIdOrderByRowNumberAsc(Long jobId, Pageable pageable);

    Page<DataImportJobRow> findByJobIdAndStatusOrderByRowNumberAsc(Long jobId, String status, Pageable pageable);

    long countByJobIdAndStatus(Long jobId, String status);

    void deleteByJobId(Long jobId);
}
