package org.example.person.service.implementations.galaxy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.person.dto.galaxy.GalaxyDto;
import org.example.person.exception.connect.ConnectException;
import org.example.person.service.api.galaxy.GalaxyService;
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
public class GalaxyServiceImpl implements GalaxyService {
    private final WebClient.Builder webClientBuilder;

    @Override
    public GalaxyDto findGalaxyBySystemId(String authorizationHeader, Long systemId) {
        return webClientBuilder
                .baseUrl("http://galaxy-service/api/v1/galaxy-app/").build()
                .get()
                .uri(uri -> uri
                        .path("systems/{systemId}/galaxies/")
                        .build(systemId)
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to find galaxy by system id {}", systemId);
                    throw new ConnectException();
                })
                .bodyToMono(GalaxyDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(
                        WebClientResponseException.Unauthorized.class,
                        error -> Mono.error(
                                new AccessDeniedException(
                                        "Вам закрыт доступ к данной функциональности бортового компьютера"
                                )
                        )
                ).block();
    }

    @Override
    public Map<Long, GalaxyDto> findGalaxiesBySystemIdIn(String authorizationHeader, List<Long> systemIds) {
        return webClientBuilder
                .baseUrl("http://galaxy-service/api/v1/galaxy-app/").build()
                .get()
                .uri(uri -> uri
                        .path("systems/galaxies/")
                        .queryParam("systemIds", systemIds)
                        .build()
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to find galaxies by system ids");
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Long, GalaxyDto>>() {
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
