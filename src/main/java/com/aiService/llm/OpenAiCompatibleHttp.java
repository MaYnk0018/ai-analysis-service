package com.aiService.llm;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * OpenAI-style POST /chat/completions (also used by Groq and many other hosts).
 */
@Slf4j
final class OpenAiCompatibleHttp {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String model;
    private final String labelPrefix;

    OpenAiCompatibleHttp(
            ObjectMapper objectMapper,
            String baseUrl,
            String bearerToken,
            String model,
            String labelPrefix) {
        this.objectMapper = objectMapper;
        this.model = model;
        this.labelPrefix = labelPrefix;
        String root = baseUrl.replaceAll("/$", "");
        WebClient.Builder b = WebClient.builder().baseUrl(root);
        if (bearerToken != null && !bearerToken.isBlank()) {
            b = b.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken.trim());
        }
        this.webClient = b.build();
    }

    String complete(String userPrompt) {
        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(Map.of("role", "user", "content", userPrompt)),
                "temperature", 0.2);

        String raw = webClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofMinutes(3))
                .block();

        if (raw == null || raw.isBlank()) {
            throw new IllegalStateException("Empty response from " + labelPrefix);
        }
        try {
            JsonNode root = objectMapper.readTree(raw);
            String content = root.path("choices").path(0).path("message").path("content").asText("");
            if (content.isBlank()) {
                JsonNode err = root.path("error");
                String msg = err.isMissingNode() ? raw : err.toString();
                throw new IllegalStateException(labelPrefix + " API error: " + msg);
            }
            return content;
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("{} parse failure: {}", labelPrefix, e.getMessage());
            throw new IllegalStateException("Invalid JSON from " + labelPrefix, e);
        }
    }

    String modelLabel() {
        return labelPrefix + ":" + model;
    }
}
