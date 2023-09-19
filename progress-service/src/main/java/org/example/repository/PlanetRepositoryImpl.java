package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.dto.planet.PlanetDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.systemEX.SystemNotFoundException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PlanetRepositoryImpl implements PlanetRepository {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderRepository authorizationHeaderRepository;

    @Override
    public List<PlanetDto> findPlanetsBySystemId(Integer systemId) {
        return webClientBuilder
                .baseUrl("http://planet-service/api/v1/planet-app/").build()
                .get()
                .uri(uri -> uri
                        .path("system/{systemId}/planet/")
                        .build(systemId)
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(PlanetDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.NotFound.class, error -> Flux.error(new SystemNotFoundException(systemId)))
                .collectList()
                .block();
    }

    @Override
    public Map<Integer, List<PlanetDto>> findPlanetsBySystemIdIn(List<Integer> systemIds) {
        return webClientBuilder
                .baseUrl("http://planet-service/api/v1/planet-app/").build()
                .get()
                .uri(uri -> uri
                        .path("planet/")
                        .queryParam("systemIds", systemIds)
                        .build()
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Integer, List<PlanetDto>>>() {
                })
                .timeout(Duration.ofSeconds(5))
                .blockLast();
    }
}
