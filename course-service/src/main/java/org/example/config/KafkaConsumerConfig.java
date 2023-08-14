package org.example.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {
    @Value("${SPRING_KAFKA_BOOTSTRAP_SERVERS}")
    private String bootstrapAddress;

    @Bean
    public ConsumerFactory<String, Object> courseFactory() {
        Map<String, Object> properties = objectProperties();
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(JsonDeserializer.TYPE_MAPPINGS, "course:org.example.dto.event.CourseCreateEvent");
        return new DefaultKafkaConsumerFactory<>(properties);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> courseKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(courseFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, Object> themeFactory() {
        Map<String, Object> properties = objectProperties();
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        properties.put(JsonDeserializer.TYPE_MAPPINGS, "theme:org.example.dto.event.CourseThemeCreateEvent");
        return new DefaultKafkaConsumerFactory<>(properties);
    }

    private Map<String, Object> objectProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "course");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return properties;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> themeKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(themeFactory());
        return factory;
    }
}