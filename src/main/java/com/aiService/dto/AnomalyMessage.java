package com.aiService.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyMessage {
    private String anomalyId;
    private String serviceId;
    private LocalDateTime detectedAt;
    private LocalDateTime windowStart;
    private LocalDateTime windowEnd;
    private int errorCount;
    private double baselineMean;
    private double baselineStddev;
    private double zScore;
    private String severity;
    private List<Map<String, Object>> logSample;
    
}
