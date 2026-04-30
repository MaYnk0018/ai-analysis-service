package com.aiService.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "incident_feedback")
@Getter
@Setter
@NoArgsConstructor
public class IncidentFeedback {
    
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "incident_id")
    private String incidentId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "rating")
    private int rating;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
