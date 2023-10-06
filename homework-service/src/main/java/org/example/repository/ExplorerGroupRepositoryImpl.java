package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.dto.explorer.ExplorerGroupDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.explorerEX.ExplorerGroupNotFoundException;
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
public class ExplorerGroupRepositoryImpl implements ExplorerGroupRepository {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderRepository authorizationHeaderRepository;

    @Override
    public Optional<ExplorerGroupDto> findById(Integer groupId) {
        return webClientBuilder
                .baseUrl("http://explorer-service/api/v1/explorer-app/").build()
                .get()
                .uri(uri -> uri
                        .path("group/{groupId}/")
                        .build(groupId)
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND), response -> {
                    throw new ConnectException();
                })
                .bodyToMono(ExplorerGroupDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.NotFound.class, error -> Mono.error(new ExplorerGroupNotFoundException(groupId)))
                .blockOptional();
    }

    @Override
    public Map<Integer, Integer> findExplorerGroupsCourseIdByGroupIdIn(List<Integer> groupIds) {
        return webClientBuilder
                .baseUrl("http://explorer-service/api/v1/explorer-app/").build()
                .get()
                .uri(uri -> uri
                        .path("groups/")
                        .queryParam("groupIds", groupIds)
                        .build()
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Integer, Integer>>() {
                })
                .timeout(Duration.ofSeconds(5))
                .blockLast();
    }
}
