package org.example.progress.config;

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
    @Qualifier("createCourseRatingOffer")
    public ProducerFactory<Long, Object> createCourseRatingOffer() {
        return jsonValueProducer();
    }

    @Bean
    @Qualifier("createCourseRatingOfferKafkaTemplate")
    public KafkaTemplate<Long, Object> createCourseRatingOfferKafkaTemplate() {
        return new KafkaTemplate<>(createCourseRatingOffer());
    }

    @Bean
    @Qualifier("createExplorerFeedbackOffer")
    public ProducerFactory<Long, Object> createExplorerFeedbackOffer() {
        return jsonValueProducer();
    }

    @Bean
    @Qualifier("createExplorerFeedbackOfferKafkaTemplate")
    public KafkaTemplate<Long, Object> createExplorerFeedbackOfferKafkaTemplate() {
        return new KafkaTemplate<>(createExplorerFeedbackOffer());
    }

    @Bean
    @Qualifier("createKeeperFeedbackOffer")
    public ProducerFactory<Long, Object> createKeeperFeedbackOffer() {
        return jsonValueProducer();
    }

    @Bean
    @Qualifier("createKeeperFeedbackOfferKafkaTemplate")
    public KafkaTemplate<Long, Object> createKeeperFeedbackOfferKafkaTemplate() {
        return new KafkaTemplate<>(createExplorerFeedbackOffer());
    }

    private ProducerFactory<Long, Object> jsonValueProducer() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(properties);
    }
}
