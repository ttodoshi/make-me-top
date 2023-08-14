package org.example.service;

import lombok.Setter;
import org.example.dto.galaxy.GalaxyInformationGetResponse;
import org.example.exception.classes.connectEX.ConnectException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Component
public class GalaxyRequestSender {
    @Value("${galaxy_app_url}")
    private String GALAXY_APP_URL;
    @Setter
    private String token;

    public GalaxyInformationGetResponse[] getGalaxies() {
        WebClient webClient = WebClient.create(GALAXY_APP_URL);
        return webClient.get()
                .uri("galaxy/")
                .header("Authorization", token)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToMono(GalaxyInformationGetResponse[].class)
                .timeout(Duration.ofSeconds(10))
                .block();
    }
}
