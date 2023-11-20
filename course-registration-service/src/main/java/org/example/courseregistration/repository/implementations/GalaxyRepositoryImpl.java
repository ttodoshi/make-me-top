package org.example.courseregistration.repository.implementations;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.dto.galaxy.GalaxyDto;
import org.example.courseregistration.repository.GalaxyRepository;
import org.example.courseregistration.utils.AuthorizationHeaderContextHolder;
import org.example.courseregistration.exception.classes.connect.ConnectException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class GalaxyRepositoryImpl implements GalaxyRepository {
    private final AuthorizationHeaderContextHolder authorizationHeaderContextHolder;
    private final WebClient.Builder webClientBuilder;

    @Override
    public GalaxyDto findGalaxyBySystemId(Integer systemId) {
        return webClientBuilder
                .baseUrl("http://galaxy-service/api/v1/galaxy-app/").build()
                .get()
                .uri(uri -> uri
                        .path("systems/{systemId}/galaxies/")
                        .build(systemId)
                )
                .header("Authorization", authorizationHeaderContextHolder.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToMono(GalaxyDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .block();
    }
}
