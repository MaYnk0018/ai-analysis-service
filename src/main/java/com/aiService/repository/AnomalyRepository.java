package com.aiService.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Phase 4 only needs to update anomaly status in the shared DB.
 * Uses native queries to avoid duplicating the full AnomalyEntity mapping here.
 */
@Repository
public interface AnomalyRepository extends CrudRepository<Object, String> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE anomalies SET status = :status WHERE id = :anomalyId", nativeQuery = true)
    int updateStatus(@Param("anomalyId") String anomalyId, @Param("status") String status);
}

