package com.aiService.claude;

import com.aiService.dto.ParsedIncident;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResponseParser {

    private final ObjectMapper objectMapper;

    public ParsedIncident parse(String response) {
        try {
            String json = unwrapPossibleMarkdownFence(response.trim());
            JsonNode root = objectMapper.readTree(json);

            return ParsedIncident.builder()
                    .hypothesis(root.get("hypothesis").asText())
                    .confidence(root.get("confidence").asDouble())
                    .affectedComponents(toStringList(root.get("affected_components")))
                    .suggestedActions(toStringList(root.get("suggested_actions")))
                    .similarIncidentIds(toStringList(root.get("similar_incident_ids")))
                    .parseSuccess(true)
                    .build();

        } catch (Exception e) {
            return ParsedIncident.builder()
                    .hypothesis("Failed to parse AI response")
                    .confidence(0.0)
                    .affectedComponents(List.of())
                    .suggestedActions(List.of())
                    .similarIncidentIds(List.of())
                    .parseSuccess(false)
                    .build();
        }
    }

    /** Local LLMs often wrap JSON in ```json ... ``` even when asked not to. */
    private static String unwrapPossibleMarkdownFence(String raw) {
        if (!raw.startsWith("```")) {
            return raw;
        }
        int firstNl = raw.indexOf('\n');
        int lastFence = raw.lastIndexOf("```");
        if (firstNl < 0 || lastFence <= firstNl) {
            return raw;
        }
        return raw.substring(firstNl + 1, lastFence).trim();
    }

    private List<String> toStringList(JsonNode arrayNode) {
        List<String> result = new ArrayList<>();
        if (arrayNode != null && arrayNode.isArray()) {
            arrayNode.forEach(item -> result.add(item.asText()));
        }
        return result;
    }
}