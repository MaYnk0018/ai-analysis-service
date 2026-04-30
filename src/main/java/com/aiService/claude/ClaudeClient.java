package com.aiService.claude;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service

public class ClaudeClient {
    // @Value("${anthropic.api-key}")
    // private String apiKey;
    public String analyze(String prompt) {
        return """
                {
                  "hypothesis": "The service is seeing an error spike, likely caused by repeated failures in the recent logs.",
                  "confidence": 0.7,
                  "affected_components": ["unknown"],
                  "suggested_actions": ["Check recent deploys", "Inspect service logs", "Verify database/network dependencies"],
                  "similar_incident_ids": []
                }
                """;
    }
}
