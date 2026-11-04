package com.bluebells.examination;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface GradingScaleRepository extends JpaRepository<GradingScale, Long> {

    @Query("SELECT g FROM GradingScale g WHERE :percentage >= g.minPercentage AND :percentage <= g.maxPercentage")
    Optional<GradingScale> findScaleForPercentage(BigDecimal percentage);

    List<GradingScale> findAllByOrderByMinPercentageDesc();
}
