package com.ptob.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptob.demo.model.EventEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
public class KafkaEventPublisher implements EventPublisher {
    private static final Logger log = LoggerFactory.getLogger(KafkaEventPublisher.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final boolean kafkaEnabled;
    private final String topicPrefix;

    public KafkaEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                               ObjectMapper objectMapper,
                               @Value("${app.kafka.enabled:true}") boolean kafkaEnabled,
                               @Value("${app.kafka.topic-prefix}") String topicPrefix) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.kafkaEnabled = kafkaEnabled;
        this.topicPrefix = topicPrefix;
    }

    @Override
    public void publish(String topicSuffix, String entityKey, Object payload) {
        try {
            EventEnvelope envelope = new EventEnvelope(topicSuffix, UUID.randomUUID().toString(), entityKey, Instant.now(),
                    objectMapper.convertValue(payload, Map.class));
            String json = objectMapper.writeValueAsString(envelope);
            String topic = topicPrefix + "." + topicSuffix;
            if (kafkaEnabled) {
                kafkaTemplate.send(topic, entityKey, json);
            } else {
                log.info("Kafka disabled. Would publish topic={} key={} payload={}", topic, entityKey, json);
            }
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to serialize event payload", e);
        }
    }
}
