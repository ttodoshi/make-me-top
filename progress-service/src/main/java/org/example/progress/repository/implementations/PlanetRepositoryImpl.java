package org.example.progress.repository.implementations;

import lombok.RequiredArgsConstructor;
import org.example.progress.dto.planet.PlanetDto;
import org.example.progress.exception.classes.connect.ConnectException;
import org.example.progress.exception.classes.system.SystemNotFoundException;
import org.example.progress.utils.AuthorizationHeaderContextHolder;
import org.example.progress.repository.PlanetRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PlanetRepositoryImpl implements PlanetRepository {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderContextHolder authorizationHeaderContextHolder;

    @Override
    public Optional<PlanetDto> findById(Long planetId) {
        return webClientBuilder
                .baseUrl("http://planet-service/api/v1/planet-app/").build()
                .get()
                .uri(uri -> uri
                        .path("planets/{planetId}/")
                        .build(planetId)
                )
                .header("Authorization", authorizationHeaderContextHolder.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND) && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToMono(PlanetDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .onErrorResume(WebClientResponseException.NotFound.class, error -> Mono.empty())
                .blockOptional();
    }

    @Override
    public List<PlanetDto> findPlanetsBySystemId(Long systemId) {
        return webClientBuilder
                .baseUrl("http://planet-service/api/v1/planet-app/").build()
                .get()
                .uri(uri -> uri
                        .path("systems/{systemId}/planets/")
                        .build(systemId)
                )
                .header("Authorization", authorizationHeaderContextHolder.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND) && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(PlanetDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .onErrorResume(WebClientResponseException.NotFound.class, error -> Flux.error(new SystemNotFoundException(systemId)))
                .collectList()
                .block();
    }

    @Override
    public Map<Long, List<PlanetDto>> findPlanetsBySystemIdIn(List<Long> systemIds) {
        return webClientBuilder
                .baseUrl("http://planet-service/api/v1/planet-app/").build()
                .get()
                .uri(uri -> uri
                        .path("systems/planets/")
                        .queryParam("systemIds", systemIds)
                        .build()
                )
                .header("Authorization", authorizationHeaderContextHolder.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Long, List<PlanetDto>>>() {
                })
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .blockLast();
    }
}
