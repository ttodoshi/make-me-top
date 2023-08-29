package org.example.repository.custom;

import lombok.RequiredArgsConstructor;
import org.example.dto.galaxy.GalaxyDTO;
import org.example.dto.galaxy.GalaxyInformationGetResponse;
import org.example.exception.classes.connectEX.ConnectException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GalaxyRepositoryImpl implements GalaxyRepository {
    private final AuthorizationHeaderRepository authorizationHeaderRepository;
    private final WebClient.Builder webClientBuilder;

    @Override
    public List<GalaxyInformationGetResponse> getGalaxies() {
        return webClientBuilder
                .baseUrl("http://galaxy-service/galaxy-app/").build()
                .get()
                .uri("galaxy/")
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(GalaxyInformationGetResponse.class)
                .timeout(Duration.ofSeconds(10))
                .collectList()
                .block();
    }

    @Override
    public GalaxyDTO getGalaxyBySystemId(Integer systemId) {
        return webClientBuilder
                .baseUrl("http://galaxy-service/galaxy-app/").build()
                .get()
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
