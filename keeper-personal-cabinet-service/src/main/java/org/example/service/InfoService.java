package org.example.service;

import lombok.Setter;
import org.example.exception.classes.connectEX.ConnectException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;

@Service
public class InfoService {
    @Value("${info_app_url}")
    private String INFO_APP_URL;
    @Setter
    private String token;

    public Map<String, Object> getKeeperPersonalCabinetInfo() {
        WebClient webClient = WebClient.create(INFO_APP_URL);
        return webClient.get()
                .uri("keeper-cabinet/")
                .header("Authorization", token)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .timeout(Duration.ofSeconds(5))
                .blockLast();
    }
}
