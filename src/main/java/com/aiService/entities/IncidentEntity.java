package com.aiService.entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "incidents")
public class IncidentEntity {
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "anomaly_id")
    private String anomalyId;

    @Column(name = "hypothesis")
    private String hypothesis;

    @Column(name = "confidence")
    private double confidence;

    @Column(name = "affected_components")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> affectedComponents;

    @Column(name = "suggested_actions")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> suggestedActions;

    @Column(name = "similar_incident_ids")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> similarIncidentIds;

    @Column(name = "raw_prompt")
    private String rawPrompt;

    @Column(name = "raw_response")
    private String rawResponse;

    @Column(name = "tokens_used")
    private int tokensUsed;

    @Column(name = "model_version")
    private String modelVersion;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
