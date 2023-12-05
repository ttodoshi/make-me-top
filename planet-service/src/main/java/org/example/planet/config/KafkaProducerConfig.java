package org.example.planet.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
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
    @Qualifier("createCourseThemeProducer")
    public ProducerFactory<Long, Object> createCourseThemeProducer() {
        return jsonValueProducer();
    }

    @Bean
    @Qualifier("createCourseThemeKafkaTemplate")
    public KafkaTemplate<Long, Object> createCourseThemeKafkaTemplate() {
        return new KafkaTemplate<>(createCourseThemeProducer());
    }

    @Bean
    @Qualifier("updateCourseThemeProducer")
    public ProducerFactory<Long, Object> updateCourseThemeProducer() {
        return jsonValueProducer();
    }

    @Bean
    @Qualifier("updateCourseThemeKafkaTemplate")
    public KafkaTemplate<Long, Object> updateCourseThemeKafkaTemplate() {
        return new KafkaTemplate<>(updateCourseThemeProducer());
    }

    private ProducerFactory<Long, Object> jsonValueProducer() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    public ProducerFactory<Long, Long> deleteCourseThemeProducer() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    public KafkaTemplate<Long, Long> deleteCourseThemeKafkaTemplate() {
        return new KafkaTemplate<>(deleteCourseThemeProducer());
    }
}
