package com.aiService.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import com.aiService.dto.AnomalyMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
@RequiredArgsConstructor

public class AnomalyConsumer {
    @KafkaListener(topics = "anomalies", groupId = "ai-group", containerFactory = "anomalyKafkaListenerContainerFactory")
    public void consume(ConsumerRecord<String, AnomalyMessage> record, Acknowledgment ack) {
        //consume , acknowledge the message, retrys
        try {
            AnomalyMessage anomalyMessage = record.value();
            //analyze the anomaly message
            analyzeAnomaly(anomalyMessage);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error consuming anomaly message", e);
            //do not acknowledge the message
        }
    }
    private void analyzeAnomaly(AnomalyMessage anomalyMessage) {
        //analyze the anomaly message
        System.out.println("Analyzing anomaly message: " + anomalyMessage);
    }
}
