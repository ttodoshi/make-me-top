package org.example.planet.repository.implementations;

import lombok.RequiredArgsConstructor;
import org.example.planet.exception.classes.connect.ConnectException;
import org.example.planet.repository.StarSystemRepository;
import org.example.planet.utils.AuthorizationHeaderContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class StarSystemRepositoryImpl implements StarSystemRepository {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderContextHolder authorizationHeaderContextHolder;

    @Override
    public Boolean existsById(Integer systemId) {
        return webClientBuilder
                .baseUrl("http://galaxy-service/api/v1/galaxy-app/").build()
                .get()
                .uri(uri -> uri
                        .path("systems/{systemId}/")
                        .build(systemId)
                )
                .header("Authorization", authorizationHeaderContextHolder.getAuthorizationHeader())
                .exchangeToMono(
                        r -> {
                            if (r.statusCode().is2xxSuccessful())
                                return Mono.just(true);
                            else if (r.statusCode().equals(HttpStatus.NOT_FOUND))
                                return Mono.just(false);
                            else return Mono.error(new ConnectException());
                        }
                )
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .block();
    }
}
