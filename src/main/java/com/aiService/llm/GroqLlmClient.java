package com.aiService.llm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Groq — OpenAI-compatible API; free tier / fast inference on groq.com (subject to their limits).
 */
@Service
@ConditionalOnProperty(name = "ai.llm.provider", havingValue = "groq")
public class GroqLlmClient implements LlmClient {

    private final OpenAiCompatibleHttp backend;

    public GroqLlmClient(
            @Value("${groq.base-url:https://api.groq.com/openai/v1}") String baseUrl,
            @Value("${groq.api-key:}") String apiKey,
            @Value("${groq.model:llama-3.3-70b-versatile}") String model,
            ObjectMapper objectMapper) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("groq.api-key / GROQ_API_KEY is required when ai.llm.provider=groq");
        }
        this.backend = new OpenAiCompatibleHttp(objectMapper, baseUrl, apiKey, model, "groq");
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
