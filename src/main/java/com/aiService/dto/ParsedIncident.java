package com.aiService.dto;

import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParsedIncident {
    private String hypothesis;
    private double confidence;
    private List<String> affectedComponents;
    private List<String> suggestedActions;
    private List<String> similarIncidentIds;
    private boolean parseSuccess;
}