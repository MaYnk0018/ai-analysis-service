package com.aiService.repository;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * Phase 4 only needs to update anomaly status in the shared DB.
 * Use JdbcTemplate to avoid needing a JPA entity mapping for the anomalies table
 * (its columns like `status` are defined as MySQL ENUM in the shared schema).
 */
@Repository
public class AnomalyRepository {

    private final JdbcTemplate jdbcTemplate;

    public AnomalyRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public int updateStatus(String anomalyId, String status) {
        return jdbcTemplate.update(
                "UPDATE anomalies SET status = ? WHERE id = ?",
                status,
                anomalyId
        );
    }
}

