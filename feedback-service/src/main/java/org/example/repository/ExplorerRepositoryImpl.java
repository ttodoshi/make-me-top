package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.dto.explorer.ExplorerDto;
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
public class ExplorerRepositoryImpl implements ExplorerRepository {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderRepository authorizationHeaderRepository;

    @Override
    public Optional<ExplorerDto> findExplorerByPersonIdAndGroup_CourseId(Integer personId, Integer courseId) {
        return webClientBuilder
                .baseUrl("http://explorer-service/api/v1/explorer-app/").build()
                .get()
                .uri(uri -> uri
                        .path("explorer/")
                        .queryParam("personId", personId)
                        .queryParam("courseId", courseId)
                        .build()
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND), response -> {
                    throw new ConnectException();
                })
                .bodyToMono(ExplorerDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.NotFound.class, error -> Mono.empty())
                .blockOptional();
    }

    @Override
    public List<ExplorerDto> findExplorersByPersonId(Integer personId) {
        return webClientBuilder
                .baseUrl("http://explorer-service/api/v1/explorer-app/").build()
                .get()
                .uri(uri -> uri
                        .path("explorer/")
                        .queryParam("personId", personId)
                        .build()
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(ExplorerDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.NotFound.class, error -> Mono.error(new PersonNotFoundException(personId)))
                .collectList()
                .block();
    }

    @Override
    public Map<Integer, List<ExplorerDto>> findExplorersByGroup_CourseIdIn(List<Integer> courseIds) {
        return webClientBuilder
                .baseUrl("http://explorer-service/api/v1/explorer-app/").build()
                .get()
                .uri(uri -> uri
                        .path("course/explorers/")
                        .queryParam("courseIds", courseIds)
                        .build()
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Integer, List<ExplorerDto>>>() {
                })
                .timeout(Duration.ofSeconds(5))
                .blockLast();
    }

    @Override
    public Map<Integer, List<ExplorerDto>> findExplorersByPersonIdIn(List<Integer> personIds) {
        return webClientBuilder
                .baseUrl("http://explorer-service/api/v1/explorer-app/").build()
                .get()
                .uri(uri -> uri
                        .path("people/explorers/")
                        .queryParam("personIds", personIds)
                        .build()
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Integer, List<ExplorerDto>>>() {
                })
                .timeout(Duration.ofSeconds(5))
                .blockLast();
    }
}