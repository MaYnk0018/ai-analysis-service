package com.aiService.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import com.aiService.dto.AnomalyMessage;
import com.aiService.dto.ParsedIncident;
import com.aiService.entities.IncidentEntity;
import com.aiService.publisher.AiResultPublisher;
import com.aiService.repository.AnomalyRepository;
import com.aiService.repository.IncidentRepository;
import com.aiService.claude.ClaudeClient;
import com.aiService.claude.ResponseParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
@RequiredArgsConstructor

public class AnomalyConsumer {

    private final ClaudeClient claudeClient;
    private final ResponseParser responseParser;
    private final IncidentRepository incidentRepository;
    private final AnomalyRepository anomalyRepository;
    private final AiResultPublisher aiResultPublisher;

    @KafkaListener(topics = "anomalies", groupId = "ai-group", containerFactory = "anomalyKafkaListenerContainerFactory")
    public void consume(ConsumerRecord<String, AnomalyMessage> record, Acknowledgment ack) {
        //consume , acknowledge the message, retrys
        try {
            AnomalyMessage anomalyMessage = record.value();
            runAnalysis(anomalyMessage);
            // For Phase 4: always advance the offset once we attempted processing.
            // Dispatch failures are recorded in DB / logs instead of re-processing forever.
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error consuming anomaly message", e);
            //do not acknowledge the message
        }
    }

    private void runAnalysis(AnomalyMessage anomaly) {
        String prompt = buildPrompt(anomaly);

        anomalyRepository.updateStatus(anomaly.getAnomalyId(), "ANALYZING");

        String raw = claudeClient.analyze(prompt);
        ParsedIncident parsed = responseParser.parse(raw);

        IncidentEntity entity = new IncidentEntity();
        entity.setAnomalyId(anomaly.getAnomalyId());
        entity.setHypothesis(parsed.getHypothesis());
        entity.setConfidence(parsed.getConfidence());
        entity.setAffectedComponents(parsed.getAffectedComponents());
        entity.setSuggestedActions(parsed.getSuggestedActions());
        entity.setSimilarIncidentIds(parsed.getSimilarIncidentIds());
        entity.setRawPrompt(prompt);
        entity.setRawResponse(raw);
        entity.setTokensUsed(0);
        entity.setModelVersion("fake-claude");

        IncidentEntity saved = incidentRepository.save(entity);

        anomalyRepository.updateStatus(anomaly.getAnomalyId(), "RESOLVED");

        aiResultPublisher.publish(anomaly, saved, parsed);

        log.info("AI incident saved: incidentId={} anomalyId={} serviceId={}",
                saved.getId(), anomaly.getAnomalyId(), anomaly.getServiceId());
    }

    private String buildPrompt(AnomalyMessage a) {
        return """
                You are an expert Site Reliability Engineer analyzing a production anomaly.

                === ANOMALY REPORT ===
                Service: %s
                Detected At: %s
                Severity: %s
                Z-Score: %.2f (baseline mean: %.1f errors/min, current: %d errors/min)
                Window: %s to %s

                Respond ONLY with a valid JSON object. No text before or after it. No markdown fences.
                """.formatted(
                a.getServiceId(),
                a.getDetectedAt(),
                a.getSeverity(),
                a.getZScore(),
                a.getBaselineMean(),
                a.getErrorCount(),
                a.getWindowStart(),
                a.getWindowEnd()
        );
    }
}
