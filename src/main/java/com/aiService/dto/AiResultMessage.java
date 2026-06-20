package com.aiService.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiResultMessage {
    private String anomalyId;
    private String incidentId;
    private String serviceId;
    private String hypothesis;
    private double confidence;
    private List<String> affectedComponents;
    private List<String> suggestedActions;
    private String severity;
    private double zScore;
    private int errorCount;
}

