package org.example.person.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    @Qualifier("deleteProgressAndMarkByExplorerIdProducer")
    public ProducerFactory<Integer, Integer> deleteProgressAndMarkByExplorerIdProducer() {
        return new DefaultKafkaProducerFactory<>(deleteProducerProperties());
    }

    @Bean
    @Qualifier("deleteProgressAndMarkByExplorerIdKafkaTemplate")
    public KafkaTemplate<Integer, Integer> deleteProgressAndMarkByExplorerIdKafkaTemplate() {
        return new KafkaTemplate<>(deleteProgressAndMarkByExplorerIdProducer());
    }

    @Bean
    @Qualifier("deleteFeedbackByExplorerIdProducer")
    public ProducerFactory<Integer, Integer> deleteFeedbackByExplorerIdProducer() {
        return new DefaultKafkaProducerFactory<>(deleteProducerProperties());
    }

    @Bean
    @Qualifier("deleteFeedbackByExplorerIdKafkaTemplate")
    public KafkaTemplate<Integer, Integer> deleteFeedbackByExplorerIdKafkaTemplate() {
        return new KafkaTemplate<>(deleteFeedbackByExplorerIdProducer());
    }

    private Map<String, Object> deleteProducerProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        return properties;
    }
}
