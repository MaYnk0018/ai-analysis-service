package com.aiService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aiService.entities.IncidentEntity;

@Repository
public interface IncidentRepository extends JpaRepository<IncidentEntity, String> {
    IncidentEntity findByAnomalyId(String anomalyId);
}
