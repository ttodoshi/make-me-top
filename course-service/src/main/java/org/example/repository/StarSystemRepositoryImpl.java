package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.dto.starsystem.StarSystemDTO;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class StarSystemRepositoryImpl implements StarSystemRepository {
    private final AuthorizationHeaderRepository authorizationHeaderRepository;

    private final WebClient.Builder webClientBuilder;

    public StarSystemDTO[] getSystemsByGalaxyId(Integer galaxyId) {
        return webClientBuilder
                .baseUrl("http://galaxy-service/galaxy-app/").build()
                .get()
                .uri("galaxy/" + galaxyId + "/system/")
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
                    throw new GalaxyNotFoundException(galaxyId);
                })
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToMono(StarSystemDTO[].class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }
}
