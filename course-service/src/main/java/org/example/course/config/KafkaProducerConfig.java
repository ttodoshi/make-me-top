package org.example.course.config;

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

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    @Qualifier("updateSystemProducer")
    public ProducerFactory<Long, String> updateSystemProducer() {
        return defaultProperties();
    }

    @Bean
    @Qualifier("updateSystemKafkaTemplate")
    public KafkaTemplate<Long, String> updateSystemKafkaTemplate() {
        return new KafkaTemplate<>(updateSystemProducer());
    }

    @Bean
    @Qualifier("updatePlanetProducer")
    public ProducerFactory<Long, String> updatePlanetProducer() {
        return defaultProperties();
    }

    @Bean
    @Qualifier("updatePlanetKafkaTemplate")
    public KafkaTemplate<Long, String> updatePlanetKafkaTemplate() {
        return new KafkaTemplate<>(updatePlanetProducer());
    }

    private ProducerFactory<Long, String> defaultProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    @Qualifier("deleteKeepersProducer")
    public ProducerFactory<Long, Long> deleteKeepersProducer() {
        return new DefaultKafkaProducerFactory<>(deleteProducerProperties());
    }

    @Bean
    @Qualifier("deleteKeepersKafkaTemplate")
    public KafkaTemplate<Long, Long> deleteKeepersKafkaTemplate() {
        return new KafkaTemplate<>(deleteKeepersProducer());
    }

    @Bean
    @Qualifier("deleteRequestsProducer")
    public ProducerFactory<Long, Long> deleteRequestsProducer() {
        return new DefaultKafkaProducerFactory<>(deleteProducerProperties());
    }

    @Bean
    @Qualifier("deleteRequestsKafkaTemplate")
    public KafkaTemplate<Long, Long> deleteRequestsKafkaTemplate() {
        return new KafkaTemplate<>(deleteRequestsProducer());
    }

    @Bean
    @Qualifier("deleteExplorersProgressProducer")
    public ProducerFactory<Long, Long> deleteExplorersProgressProducer() {
        return new DefaultKafkaProducerFactory<>(deleteProducerProperties());
    }

    @Bean
    @Qualifier("deleteExplorersProgressKafkaTemplate")
    public KafkaTemplate<Long, Long> deleteExplorersProgressKafkaTemplate() {
        return new KafkaTemplate<>(deleteExplorersProgressProducer());
    }

    @Bean
    @Qualifier("deleteHomeworksProducer")
    public ProducerFactory<Long, Long> deleteHomeworksProducer() {
        return new DefaultKafkaProducerFactory<>(deleteProducerProperties());
    }

    @Bean
    @Qualifier("deleteHomeworksKafkaTemplate")
    public KafkaTemplate<Long, Long> deleteHomeworksKafkaTemplate() {
        return new KafkaTemplate<>(deleteHomeworksProducer());
    }

    private Map<String, Object> deleteProducerProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        return properties;
    }
}
