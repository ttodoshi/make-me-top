package org.example.galaxy.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public ProducerFactory<String, Object> createCourseProducer() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    public KafkaTemplate<String, Object> createCourseKafkaTemplate() {
        return new KafkaTemplate<>(createCourseProducer());
    }

    @Bean
    public ProducerFactory<Long, String> updateCourseProducer() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    public KafkaTemplate<Long, String> updateCourseKafkaTemplate() {
        return new KafkaTemplate<>(updateCourseProducer());
    }

    @Bean
    @Qualifier("deleteCourseProducer")
    public ProducerFactory<Long, Long> deleteCourseProducer() {
        return new DefaultKafkaProducerFactory<>(deleteProducerProperties());
    }

    @Bean
    @Qualifier("deleteCourseKafkaTemplate")
    public KafkaTemplate<Long, Long> deleteCourseKafkaTemplate() {
        return new KafkaTemplate<>(deleteCourseProducer());
    }

    @Bean
    @Qualifier("deletePlanetsProducer")
    public ProducerFactory<Long, Long> deletePlanetsProducer() {
        return new DefaultKafkaProducerFactory<>(deleteProducerProperties());
    }

    private Map<String, Object> deleteProducerProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        return properties;
    }

    @Bean
    @Qualifier("deletePlanetsKafkaTemplate")
    public KafkaTemplate<Long, Long> deletePlanetsKafkaTemplate() {
        return new KafkaTemplate<>(deletePlanetsProducer());
    }
}
