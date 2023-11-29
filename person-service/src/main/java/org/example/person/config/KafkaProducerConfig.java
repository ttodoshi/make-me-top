package org.example.person.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
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
    public ProducerFactory<Long, Long> deleteProgressAndMarkByExplorerIdProducer() {
        return new DefaultKafkaProducerFactory<>(deleteProducerProperties());
    }

    @Bean
    @Qualifier("deleteProgressAndMarkByExplorerIdKafkaTemplate")
    public KafkaTemplate<Long, Long> deleteProgressAndMarkByExplorerIdKafkaTemplate() {
        return new KafkaTemplate<>(deleteProgressAndMarkByExplorerIdProducer());
    }

    @Bean
    @Qualifier("deleteFeedbackByExplorerIdProducer")
    public ProducerFactory<Long, Long> deleteFeedbackByExplorerIdProducer() {
        return new DefaultKafkaProducerFactory<>(deleteProducerProperties());
    }

    @Bean
    @Qualifier("deleteFeedbackByExplorerIdKafkaTemplate")
    public KafkaTemplate<Long, Long> deleteFeedbackByExplorerIdKafkaTemplate() {
        return new KafkaTemplate<>(deleteFeedbackByExplorerIdProducer());
    }

    private Map<String, Object> deleteProducerProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        return properties;
    }
}
