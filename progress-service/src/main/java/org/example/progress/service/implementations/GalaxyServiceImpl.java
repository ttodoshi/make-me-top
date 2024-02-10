package org.example.progress.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.progress.dto.galaxy.GetGalaxyDto;
import org.example.progress.exception.connect.ConnectException;
import org.example.progress.exception.galaxy.GalaxyNotFoundException;
import org.example.progress.service.GalaxyService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class GalaxyServiceImpl implements GalaxyService {
    private final WebClient.Builder webClientBuilder;

    @Override
    public GetGalaxyDto findGalaxyById(Long galaxyId) {
        return webClientBuilder
                .baseUrl("http://galaxy-service/api/v1/galaxy-app/").build()
                .get()
                .uri(uri -> uri
                        .path("galaxies/{galaxyId}/")
                        .build(galaxyId)
                )
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED) && !httpStatus.equals(HttpStatus.NOT_FOUND), response -> {
                    log.error("failed to find galaxy by id {}", galaxyId);
                    throw new ConnectException();
                })
                .bodyToMono(GetGalaxyDto.class)
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
                            log.warn("galaxy by id {} not found", galaxyId);
                            return Mono.error(
                                    new GalaxyNotFoundException(galaxyId)
                            );
                        }
                ).block();
    }
}
