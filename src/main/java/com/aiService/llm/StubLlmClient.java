package com.aiService.llm;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Offline fallback: returns fixed JSON (no network, no cost).
 */
@Service
@ConditionalOnProperty(name = "ai.llm.provider", havingValue = "stub", matchIfMissing = true)
public class StubLlmClient implements LlmClient {

    @Override
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

    @Override
    public String modelLabel() {
        return "stub";
    }
}
