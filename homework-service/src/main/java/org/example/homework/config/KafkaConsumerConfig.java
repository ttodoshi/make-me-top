package org.example.homework.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public ConsumerFactory<Long, Long> deleteHomeworksFactory() {
        return new DefaultKafkaConsumerFactory<>(deleteFactoryProperties());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, Long> deleteHomeworksKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, Long> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(deleteHomeworksFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<Long, Long> deleteHomeworkRequestByExplorerIdFactory() {
        return new DefaultKafkaConsumerFactory<>(deleteFactoryProperties());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, Long> deleteHomeworkRequestByExplorerIdKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, Long> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(deleteHomeworksFactory());
        return factory;
    }

    private Map<String, Object> deleteFactoryProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "homework");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        return properties;
    }
}