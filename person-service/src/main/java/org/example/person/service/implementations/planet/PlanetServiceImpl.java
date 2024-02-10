package org.example.person.service.implementations.planet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.person.dto.planet.PlanetDto;
import org.example.person.exception.connect.ConnectException;
import org.example.person.service.api.planet.PlanetService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanetServiceImpl implements PlanetService {
    private final WebClient.Builder webClientBuilder;

    @Override
    public Map<Long, PlanetDto> findPlanetsByPlanetIdIn(String authorizationHeader, List<Long> planetIds) {
        return webClientBuilder
                .baseUrl("http://planet-service/api/v1/planet-app/").build()
                .get()
                .uri(uri -> uri
                        .path("planets/")
                        .queryParam("planetIds", planetIds)
                        .build()
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to find planets by planet ids");
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Long, PlanetDto>>() {
                })
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(
                        WebClientResponseException.Unauthorized.class,
                        error -> Mono.error(
                                new AccessDeniedException(
                                        "Вам закрыт доступ к данной функциональности бортового компьютера"
                                )
                        )
                ).blockLast();
    }
}
