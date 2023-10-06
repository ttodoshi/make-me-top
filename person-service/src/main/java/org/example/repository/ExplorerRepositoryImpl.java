package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.dto.explorer.ExplorerDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExplorerRepositoryImpl implements ExplorerRepository {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderRepository authorizationHeaderRepository;

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
    public ExplorerDto getReferenceById(Integer explorerId) {
        return webClientBuilder
                .baseUrl("http://explorer-service/api/v1/explorer-app/").build()
                .get()
                .uri(uri -> uri
                        .path("explorer/{explorerId}/")
                        .build(explorerId)
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND), response -> {
                    throw new ConnectException();
                })
                .bodyToMono(ExplorerDto.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }
}
