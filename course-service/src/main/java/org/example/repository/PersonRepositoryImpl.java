package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.dto.person.PersonDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
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
public class PersonRepositoryImpl implements PersonRepository {
    private final AuthorizationHeaderRepository authorizationHeaderRepository;
    private final WebClient.Builder webClientBuilder;

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
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND), response -> {
                    throw new ConnectException();
                })
                .bodyToMono(PersonDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.NotFound.class, error -> Mono.empty())
                .blockOptional();
    }

    @Override
    public Map<Integer, PersonDto> findPeopleByPersonIdIn(List<Integer> personIds) {
        return webClientBuilder
                .baseUrl("http://person-service/api/v1/person-app/").build()
                .get()
                .uri(uri -> uri
                        .path("people/")
                        .queryParam("personIds", personIds)
                        .build()
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Integer, PersonDto>>() {
                })
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.NotFound.class, error -> Flux.error(new PersonNotFoundException()))
                .blockLast();
    }
}