package com.ptob.demo.persistence;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "audit_events", indexes = {
        @Index(name = "idx_audit_topic", columnList = "topic"),
        @Index(name = "idx_audit_received", columnList = "receivedAt")
})
public class AuditEventEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String topic;
    private String entityKey;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String payload;
    private Instant receivedAt;

    public AuditEventEntity() {}
    public AuditEventEntity(String topic, String entityKey, String payload, Instant receivedAt) {
        this.topic = topic; this.entityKey = entityKey; this.payload = payload; this.receivedAt = receivedAt;
    }
    public Long getId() { return id; }
    public String getTopic() { return topic; }
    public String getEntityKey() { return entityKey; }
    public String getPayload() { return payload; }
    public Instant getReceivedAt() { return receivedAt; }
}
