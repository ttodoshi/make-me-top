package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.dto.keeper.KeeperDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class KeeperRepositoryImpl implements KeeperRepository {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderRepository authorizationHeaderRepository;

    @Override
    public Optional<KeeperDto> findKeeperByPersonIdAndCourseId(Integer personId, Integer courseId) {
        return webClientBuilder
                .baseUrl("http://person-service/api/v1/person-app/").build()
                .get()
                .uri(uri -> uri
                        .path("keeper/")
                        .queryParam("personId", personId)
                        .queryParam("courseId", courseId)
                        .build()
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND), response -> {
                    throw new ConnectException();
                })
                .bodyToMono(KeeperDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.NotFound.class, error -> Mono.empty())
                .blockOptional();
    }

    @Override
    public List<KeeperDto> findKeepersByPersonId(Integer personId) {
        return webClientBuilder
                .baseUrl("http://person-service/api/v1/person-app/").build()
                .get()
                .uri(uri -> uri
                        .path("keeper/")
                        .queryParam("personId", personId)
                        .build()
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(KeeperDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.NotFound.class, error -> Mono.error(new PersonNotFoundException(personId)))
                .collectList()
                .block();
    }

    @Override
    public Map<Integer, List<KeeperDto>> findKeepersByPersonIdIn(List<Integer> personIds) {
        return webClientBuilder
                .baseUrl("http://person-service/api/v1/person-app/").build()
                .get()
                .uri(uri -> uri
                        .path("people/keepers/")
                        .queryParam("personIds", personIds)
                        .build()
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Integer, List<KeeperDto>>>() {
                })
                .timeout(Duration.ofSeconds(5))
                .blockLast();
    }
}
