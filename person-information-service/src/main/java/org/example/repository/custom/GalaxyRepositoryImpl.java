package org.example.repository.custom;

import lombok.RequiredArgsConstructor;
import org.example.dto.galaxy.GalaxyDTO;
import org.example.dto.galaxy.GalaxyInformationGetResponse;
import org.example.exception.classes.connectEX.ConnectException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class GalaxyRepositoryImpl implements GalaxyRepository {
    private final AuthorizationHeaderRepository authorizationHeaderRepository;
    @Value("${galaxy_app_url}")
    private String GALAXY_APP_URL;

    @Override
    public GalaxyInformationGetResponse[] getGalaxies() {
        WebClient webClient = WebClient.create(GALAXY_APP_URL);
        return webClient.get()
                .uri("galaxy/")
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToMono(GalaxyInformationGetResponse[].class)
                .timeout(Duration.ofSeconds(10))
                .block();
    }

    @Override
    public GalaxyDTO getGalaxyBySystemId(Integer systemId) {
        WebClient webClient = WebClient.create(GALAXY_APP_URL);
        return webClient.get()
                .uri("system/" + systemId + "/galaxy/")
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToMono(GalaxyDTO.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }
}
