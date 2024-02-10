package org.example.progress.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.progress.dto.planet.PlanetDto;
import org.example.progress.exception.connect.ConnectException;
import org.example.progress.exception.planet.PlanetNotFoundException;
import org.example.progress.exception.system.SystemNotFoundException;
import org.example.progress.service.PlanetService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
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
    public PlanetDto findById(String authorizationHeader, Long planetId) {
        return webClientBuilder
                .baseUrl("http://planet-service/api/v1/planet-app/").build()
                .get()
                .uri(uri -> uri
                        .path("planets/{planetId}/")
                        .build(planetId)
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND) && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to find planet by id {}", planetId);
                    throw new ConnectException();
                })
                .bodyToMono(PlanetDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(
                        WebClientResponseException.Unauthorized.class,
                        error -> Mono.error(
                                new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера"
                                )
                        )
                ).onErrorResume(
                        WebClientResponseException.NotFound.class,
                        error -> {
                            log.warn("planet by id {} not found", planetId);
                            return Mono.error(
                                    new PlanetNotFoundException(planetId)
                            );
                        }
                ).block();
    }

    @Override
    public List<PlanetDto> findPlanetsBySystemId(String authorizationHeader, Long systemId) {
        return webClientBuilder
                .baseUrl("http://planet-service/api/v1/planet-app/").build()
                .get()
                .uri(uri -> uri
                        .path("systems/{systemId}/planets/")
                        .build(systemId)
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND) && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to find planets by system id {}", systemId);
                    throw new ConnectException();
                })
                .bodyToFlux(PlanetDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(
                        WebClientResponseException.Unauthorized.class,
                        error -> Mono.error(
                                new AccessDeniedException(
                                        "Вам закрыт доступ к данной функциональности бортового компьютера"
                                )
                        )
                ).onErrorResume(
                        WebClientResponseException.NotFound.class,
                        error -> {
                            log.warn("system by id {} not found", systemId);
                            return Flux.error(
                                    new SystemNotFoundException(systemId)
                            );
                        }
                ).collectList()
                .block();
    }

    @Override
    public Map<Long, List<PlanetDto>> findPlanetsBySystemIdIn(String authorizationHeader, List<Long> systemIds) {
        return webClientBuilder
                .baseUrl("http://planet-service/api/v1/planet-app/").build()
                .get()
                .uri(uri -> uri
                        .path("systems/planets/")
                        .queryParam("systemIds", systemIds)
                        .build()
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to find planets by system ids");
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Long, List<PlanetDto>>>() {
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
