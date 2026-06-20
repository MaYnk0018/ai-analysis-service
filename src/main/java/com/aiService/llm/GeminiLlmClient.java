package com.aiService.llm;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Google Gemini — Google AI Studio API key; free tier limits apply per Google&apos;s policy.
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "ai.llm.provider", havingValue = "gemini")
public class GeminiLlmClient implements LlmClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String model;
    private final String apiKey;

    public GeminiLlmClient(
            @Value("${gemini.base-url:https://generativelanguage.googleapis.com}") String baseUrl,
            @Value("${gemini.api-key:}") String apiKey,
            @Value("${gemini.model:gemini-2.0-flash}") String model,
            WebClient.Builder webClientBuilder,
            ObjectMapper objectMapper) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("gemini.api-key / GEMINI_API_KEY is required when ai.llm.provider=gemini");
        }
        this.apiKey = apiKey.trim();
        this.model = model;
        this.objectMapper = objectMapper;
        this.webClient = webClientBuilder
                .baseUrl(baseUrl.replaceAll("/$", ""))
                .build();
    }

    @Override
    public String analyze(String prompt) {
        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))));

        String raw = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/{modelId}:generateContent")
                        .queryParam("key", apiKey)
                        .build(model))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofMinutes(3))
                .block();

        if (raw == null || raw.isBlank()) {
            throw new IllegalStateException("Empty response from Gemini");
        }
        try {
            JsonNode root = objectMapper.readTree(raw);
            JsonNode candidates = root.path("candidates");
            if (!candidates.isArray() || candidates.isEmpty()) {
                JsonNode err = root.path("error");
                String msg = err.isMissingNode() ? raw : err.toString();
                throw new IllegalStateException("Gemini API error: " + msg);
            }
            String text = candidates.get(0).path("content").path("parts").path(0).path("text").asText("");
            if (text.isBlank()) {
                throw new IllegalStateException("Gemini response missing text: " + raw);
            }
            return text;
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Gemini parse failure: {}", e.getMessage());
            throw new IllegalStateException("Invalid JSON from Gemini", e);
        }
    }

    @Override
    public String modelLabel() {
        return "gemini:" + model;
    }
}
