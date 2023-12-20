package org.example.person.repository.implementations;

import lombok.RequiredArgsConstructor;
import org.example.person.dto.planet.PlanetDto;
import org.example.person.exception.classes.connect.ConnectException;
import org.example.person.repository.PlanetRepository;
import org.example.person.utils.AuthorizationHeaderContextHolder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PlanetRepositoryImpl implements PlanetRepository {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderContextHolder authorizationHeaderContextHolder;

    @Override
    public Map<Long, PlanetDto> findPlanetsByPlanetIdIn(List<Long> planetIds) {
        return webClientBuilder
                .baseUrl("http://planet-service/api/v1/planet-app/").build()
                .get()
                .uri(uri -> uri
                        .path("planets/")
                        .queryParam("planetIds", planetIds)
                        .build()
                )
                .header("Authorization", authorizationHeaderContextHolder.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Long, PlanetDto>>() {
                })
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .blockLast();
    }
}
