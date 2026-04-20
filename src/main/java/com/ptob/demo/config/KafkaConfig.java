package com.ptob.demo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaConfig {
    @Bean NewTopic ordersTopic(@Value("${app.kafka.topic-prefix}") String prefix) {
        return TopicBuilder.name(prefix + ".trading.orders").partitions(3).replicas(1).build();
    }
    @Bean NewTopic tradesTopic(@Value("${app.kafka.topic-prefix}") String prefix) {
        return TopicBuilder.name(prefix + ".trading.trades").partitions(3).replicas(1).build();
    }
    @Bean NewTopic ledgerTopic(@Value("${app.kafka.topic-prefix}") String prefix) {
        return TopicBuilder.name(prefix + ".ledger.entries").partitions(3).replicas(1).build();
    }
    @Bean NewTopic omnibusTopic(@Value("${app.kafka.topic-prefix}") String prefix) {
        return TopicBuilder.name(prefix + ".omnibus.allocations").partitions(3).replicas(1).build();
    }
    @Bean NewTopic settlementTopic(@Value("${app.kafka.topic-prefix}") String prefix) {
        return TopicBuilder.name(prefix + ".settlement.transfers").partitions(3).replicas(1).build();
    }
}
