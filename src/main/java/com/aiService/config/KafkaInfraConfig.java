package com.aiService.config;

import java.util.Map;
import java.util.HashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import com.aiService.dto.AnomalyMessage;


@Configuration
@EnableKafka
public class KafkaInfraConfig {
    
        //usng manual ack mode to ensure that we only acknowledge messages after successful processing
    @Bean
    public ConsumerFactory<String, AnomalyMessage> anomalyConsumerFactory(@Value("${spring.kafka.bootstrap-servers}") String bootstrap){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "ai-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        JsonDeserializer<AnomalyMessage> deser = new JsonDeserializer<>(AnomalyMessage.class);
        deser.addTrustedPackages("com.aiService.dto");
        deser.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deser);
    }

    @Bean(name = "anomalyKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, AnomalyMessage>
            anomalyKafkaListenerContainerFactory(
                    ConsumerFactory<String, AnomalyMessage> anomalyConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, AnomalyMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(anomalyConsumerFactory);
        factory.getContainerProperties()
                .setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }
}