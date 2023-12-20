package org.example.course.repository.implementations;

import lombok.RequiredArgsConstructor;
import org.example.course.dto.system.StarSystemDto;
import org.example.course.exception.classes.connect.ConnectException;
import org.example.course.exception.classes.galaxy.GalaxyNotFoundException;
import org.example.course.utils.AuthorizationHeaderContextHolder;
import org.example.course.repository.StarSystemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StarSystemRepositoryImpl implements StarSystemRepository {
    private final AuthorizationHeaderContextHolder authorizationHeaderContextHolder;

    private final WebClient.Builder webClientBuilder;

    public List<StarSystemDto> findStarSystemsByGalaxyId(Long galaxyId) {
        return webClientBuilder
                .baseUrl("http://galaxy-service/api/v1/galaxy-app/").build()
                .get()
                .uri(uri -> uri
                        .path("galaxies/{galaxyId}/systems/")
                        .build(galaxyId)
                )
                .header("Authorization", authorizationHeaderContextHolder.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
                    throw new GalaxyNotFoundException(galaxyId);
                })
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(StarSystemDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .collectList()
                .block();
    }
}
