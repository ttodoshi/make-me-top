package org.example.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    @LoadBalanced
    @Qualifier("loadBalancedWebClientBuilder")
    public WebClient.Builder loadBalancedWebClient() {
        return WebClient.builder();
    }

    @Bean
    @Qualifier("webClientBuilder")
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
