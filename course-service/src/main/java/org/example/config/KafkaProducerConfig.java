package org.example.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
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
    @Qualifier("updateSystemProducer")
    public ProducerFactory<Integer, String> updateSystemProducer() {
        return defaultProperties();
    }

    @Bean
    @Qualifier("updateSystemKafkaTemplate")
    public KafkaTemplate<Integer, String> updateSystemKafkaTemplate() {
        return new KafkaTemplate<>(updateSystemProducer());
    }

    @Bean
    @Qualifier("updatePlanetProducer")
    public ProducerFactory<Integer, String> updatePlanetProducer() {
        return defaultProperties();
    }

    @Bean
    @Qualifier("updatePlanetKafkaTemplate")
    public KafkaTemplate<Integer, String> updatePlanetKafkaTemplate() {
        return new KafkaTemplate<>(updatePlanetProducer());
    }

    private ProducerFactory<Integer, String> defaultProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(properties);
    }
}
