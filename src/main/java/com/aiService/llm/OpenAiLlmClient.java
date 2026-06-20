package com.aiService.llm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * OpenAI Chat Completions — use {@code gpt-4o-mini} or similar; billing applies per OpenAI pricing.
 */
@Service
@ConditionalOnProperty(name = "ai.llm.provider", havingValue = "openai")
public class OpenAiLlmClient implements LlmClient {

    private final OpenAiCompatibleHttp backend;

    public OpenAiLlmClient(
            @Value("${openai.base-url:https://api.openai.com/v1}") String baseUrl,
            @Value("${openai.api-key:}") String apiKey,
            @Value("${openai.model:gpt-4o-mini}") String model,
            ObjectMapper objectMapper) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("openai.api-key / OPENAI_API_KEY is required when ai.llm.provider=openai");
        }
        this.backend = new OpenAiCompatibleHttp(objectMapper, baseUrl, apiKey, model, "openai");
    }

    @Override
    public String analyze(String prompt) {
        return backend.complete(prompt);
    }

    @Override
    public String modelLabel() {
        return backend.modelLabel();
    }
}
