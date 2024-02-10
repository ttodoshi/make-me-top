package org.example.planet.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.planet.exception.connect.ConnectException;
import org.example.planet.service.StarSystemService;
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
public class StarSystemServiceImpl implements StarSystemService {
    private final WebClient.Builder webClientBuilder;

    @Override
    public Boolean existsById(String authorizationHeader, Long systemId) {
        return webClientBuilder
                .baseUrl("http://galaxy-service/api/v1/galaxy-app/").build()
                .get()
                .uri(uri -> uri
                        .path("systems/{systemId}/")
                        .build(systemId)
                )
                .header("Authorization", authorizationHeader)
                .exchangeToMono(
                        r -> {
                            if (r.statusCode().is2xxSuccessful())
                                return Mono.just(true);
                            else if (r.statusCode().equals(HttpStatus.NOT_FOUND))
                                return Mono.just(false);
                            else {
                                log.error("failed to get system by id {}", systemId);
                                return Mono.error(new ConnectException());
                            }
                        }
                )
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
