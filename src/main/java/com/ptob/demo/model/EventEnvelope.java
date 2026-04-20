package com.ptob.demo.model;

import java.time.Instant;
import java.util.Map;

public record EventEnvelope(String eventType, String eventId, String entityKey, Instant occurredAt, Map<String, Object> payload) {}
