package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.repository.custom.AuthorizationHeaderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InfoService {
    private final AuthorizationHeaderRepository authorizationHeaderRepository;
    @Value("${info_app_url}")
    private String INFO_APP_URL;

    public Map<String, Object> getExplorerPersonalCabinetInfo() {
        WebClient webClient = WebClient.create(INFO_APP_URL);
        return webClient.get()
                .uri("explorer-cabinet/")
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
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
