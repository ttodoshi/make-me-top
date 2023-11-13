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

    @Bean
    @Qualifier("deleteGroupsProducer")
    public ProducerFactory<Integer, Integer> deleteGroupsProducer() {
        return new DefaultKafkaProducerFactory<>(deleteProducerProperties());
    }

    @Bean
    @Qualifier("deleteGroupsKafkaTemplate")
    public KafkaTemplate<Integer, Integer> deleteGroupsKafkaTemplate() {
        return new KafkaTemplate<>(deleteGroupsProducer());
    }

    @Bean
    @Qualifier("deleteKeepersProducer")
    public ProducerFactory<Integer, Integer> deleteKeepersProducer() {
        return new DefaultKafkaProducerFactory<>(deleteProducerProperties());
    }

    @Bean
    @Qualifier("deleteKeepersKafkaTemplate")
    public KafkaTemplate<Integer, Integer> deleteKeepersKafkaTemplate() {
        return new KafkaTemplate<>(deleteKeepersProducer());
    }

    @Bean
    @Qualifier("deleteRequestsProducer")
    public ProducerFactory<Integer, Integer> deleteRequestsProducer() {
        return new DefaultKafkaProducerFactory<>(deleteProducerProperties());
    }

    @Bean
    @Qualifier("deleteRequestsKafkaTemplate")
    public KafkaTemplate<Integer, Integer> deleteRequestsKafkaTemplate() {
        return new KafkaTemplate<>(deleteRequestsProducer());
    }

    @Bean
    @Qualifier("deleteExplorersProgressProducer")
    public ProducerFactory<Integer, Integer> deleteExplorersProgressProducer() {
        return new DefaultKafkaProducerFactory<>(deleteProducerProperties());
    }

    @Bean
    @Qualifier("deleteExplorersProgressKafkaTemplate")
    public KafkaTemplate<Integer, Integer> deleteExplorersProgressKafkaTemplate() {
        return new KafkaTemplate<>(deleteExplorersProgressProducer());
    }

    @Bean
    @Qualifier("deleteHomeworksProducer")
    public ProducerFactory<Integer, Integer> deleteHomeworksProducer() {
        return new DefaultKafkaProducerFactory<>(deleteProducerProperties());
    }

    @Bean
    @Qualifier("deleteHomeworksKafkaTemplate")
    public KafkaTemplate<Integer, Integer> deleteHomeworksKafkaTemplate() {
        return new KafkaTemplate<>(deleteHomeworksProducer());
    }

    private Map<String, Object> deleteProducerProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        return properties;
    }
}
