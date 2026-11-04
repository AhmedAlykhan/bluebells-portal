package com.bluebells.dataimport.repository;

import com.bluebells.dataimport.entity.DataImportError;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataImportErrorRepository extends JpaRepository<DataImportError, Long> {

    List<DataImportError> findByJobIdOrderByRowNumberAsc(Long jobId);

    Page<DataImportError> findByJobIdOrderByRowNumberAsc(Long jobId, Pageable pageable);

    List<DataImportError> findByJobIdAndRowNumber(Long jobId, int rowNumber);

    void deleteByJobId(Long jobId);
}
