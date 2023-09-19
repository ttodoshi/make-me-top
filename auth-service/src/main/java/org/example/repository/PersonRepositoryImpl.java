package org.example.repository;

import org.example.dto.PersonDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

@Component
public class PersonRepositoryImpl implements PersonRepository {
    private final AuthorizationHeaderRepository authorizationHeaderRepository;
    private final WebClient.Builder webClientBuilder;

    public PersonRepositoryImpl(@Qualifier("authorizationHeaderRepository") AuthorizationHeaderRepository authorizationHeaderRepository,
                                @Qualifier("loadBalancedWebClientBuilder") WebClient.Builder webClientBuilder) {
        this.authorizationHeaderRepository = authorizationHeaderRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public Optional<PersonDto> findById(Integer personId) {
        return webClientBuilder
                .baseUrl("http://person-service/api/v1/person-app/").build()
                .get()
                .uri(uri -> uri
                        .path("person/{personId}/")
                        .build(personId)
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToMono(PersonDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.empty())
                .blockOptional();
    }
}
