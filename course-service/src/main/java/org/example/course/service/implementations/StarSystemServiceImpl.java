package org.example.course.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.course.dto.system.StarSystemDto;
import org.example.course.exception.connect.ConnectException;
import org.example.course.exception.galaxy.GalaxyNotFoundException;
import org.example.course.service.StarSystemService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StarSystemServiceImpl implements StarSystemService {
    private final WebClient.Builder webClientBuilder;

    public List<StarSystemDto> findStarSystemsByGalaxyId(String authorizationHeader, Long galaxyId) {
        return webClientBuilder
                .baseUrl("http://galaxy-service/api/v1/galaxy-app/").build()
                .get()
                .uri(uri -> uri
                        .path("galaxies/{galaxyId}/systems/")
                        .build(galaxyId)
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
                    log.error("galaxy by id {} not found", galaxyId);
                    throw new GalaxyNotFoundException(galaxyId);
                })
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to get star systems by galaxy id {}", galaxyId);
                    throw new ConnectException();
                })
                .bodyToFlux(StarSystemDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(
                        WebClientResponseException.Unauthorized.class,
                        error -> Mono.error(
                                new AccessDeniedException(
                                        "Вам закрыт доступ к данной функциональности бортового компьютера"
                                )
                        )
                ).collectList()
                .block();
    }
}
