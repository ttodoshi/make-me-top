package org.example.courseregistration.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.courseregistration.dto.galaxy.GalaxyDto;
import org.example.courseregistration.exception.connect.ConnectException;
import org.example.courseregistration.service.GalaxyService;
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
                    log.error("failed to get galaxy by system id");
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
}
