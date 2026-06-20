package com.aiService.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class IncidentSummaryResponse {
    String id;
    String anomalyId;
    String hypothesis;
    double confidence;
    List<String> affectedComponents;
    List<String> suggestedActions;
    LocalDateTime createdAt;
}
