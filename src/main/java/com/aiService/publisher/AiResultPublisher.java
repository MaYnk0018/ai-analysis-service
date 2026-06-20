package com.aiService.publisher;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.aiService.dto.AiResultMessage;
import com.aiService.dto.AnomalyMessage;
import com.aiService.dto.ParsedIncident;
import com.aiService.entities.IncidentEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiResultPublisher {

    private final KafkaTemplate<String, AiResultMessage> kafkaTemplate;

    public void publish(AnomalyMessage anomaly, IncidentEntity incident, ParsedIncident parsed) {
        AiResultMessage msg = AiResultMessage.builder()
                .anomalyId(anomaly.getAnomalyId())
                .incidentId(incident.getId())
                .serviceId(anomaly.getServiceId())
                .hypothesis(parsed.getHypothesis())
                .confidence(parsed.getConfidence())
                .affectedComponents(parsed.getAffectedComponents())
                .suggestedActions(parsed.getSuggestedActions())
                .severity(anomaly.getSeverity())
                .zScore(anomaly.getZScore())
                .errorCount(anomaly.getErrorCount())
                .build();

        kafkaTemplate.send("ai-results", anomaly.getAnomalyId(), msg)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish ai-result for anomalyId={}: {}",
                                anomaly.getAnomalyId(), ex.getMessage(), ex);
                    }
                });
    }
}

