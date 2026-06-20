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
 * Free local inference via <a href="https://ollama.com">Ollama</a> — no API subscription.
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "ai.llm.provider", havingValue = "ollama")
public class OllamaLlmClient implements LlmClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String model;

    public OllamaLlmClient(
            @Value("${ollama.base-url:http://localhost:11434}") String baseUrl,
            @Value("${ollama.model:llama3.2}") String model,
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder) {
        this.model = model;
        this.objectMapper = objectMapper;
        this.webClient = webClientBuilder
                .baseUrl(baseUrl.replaceAll("/$", ""))
                .build();
    }

    @Override
    public String analyze(String prompt) {
        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "stream", false);

        String raw = webClient.post()
                .uri("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofMinutes(3))
                .block();

        if (raw == null || raw.isBlank()) {
            throw new IllegalStateException("Empty response from Ollama");
        }

        try {
            JsonNode root = objectMapper.readTree(raw);
            JsonNode message = root.path("message");
            String content = message.path("content").asText("");
            if (content.isBlank()) {
                throw new IllegalStateException("Ollama response missing message.content: " + raw);
            }
            return content;
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to parse Ollama JSON: {}", e.getMessage());
            throw new IllegalStateException("Invalid JSON from Ollama", e);
        }
    }

    @Override
    public String modelLabel() {
        return "ollama:" + model;
    }
}
