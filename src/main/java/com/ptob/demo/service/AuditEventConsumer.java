package com.ptob.demo.service;

import com.ptob.demo.persistence.AuditEventEntity;
import com.ptob.demo.persistence.AuditEventRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class AuditEventConsumer {
    private final AuditEventRepository auditEventRepository;

    public AuditEventConsumer(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    @KafkaListener(topics = {
            "${app.kafka.topic-prefix}.trading.orders",
            "${app.kafka.topic-prefix}.trading.trades",
            "${app.kafka.topic-prefix}.ledger.entries",
            "${app.kafka.topic-prefix}.omnibus.allocations",
            "${app.kafka.topic-prefix}.settlement.transfers"
    })
    public void consume(String payload,
                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                        @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        auditEventRepository.save(new AuditEventEntity(topic, key, payload, Instant.now()));
    }
}
