package com.ptob.demo.service;
public interface EventPublisher {
    void publish(String topicSuffix, String entityKey, Object payload);
}
