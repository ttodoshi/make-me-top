package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.dto.explorer.CreateExplorerGroupDto;
import org.example.dto.explorer.ExplorerGroupDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExplorerGroupRepositoryImpl implements ExplorerGroupRepository {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderRepository authorizationHeaderRepository;

    @Override
    public ExplorerGroupDto save(CreateExplorerGroupDto explorerGroup) {
        return webClientBuilder
                .baseUrl("http://person-service/api/v1/person-app/").build()
                .post()
                .uri(uri -> uri
                        .path("groups/")
                        .build()
                ).bodyValue(explorerGroup)
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToMono(ExplorerGroupDto.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }

    @Override
    public List<ExplorerGroupDto> findExplorerGroupsByKeeperIdIn(List<Integer> keeperIds) {
        return webClientBuilder
                .baseUrl("http://person-service/api/v1/person-app/").build()
                .get()
                .uri(uri -> uri
                        .path("group/")
                        .queryParam("keeperIds", keeperIds)
                        .build()
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(ExplorerGroupDto.class)
                .timeout(Duration.ofSeconds(5))
                .collectList()
                .block();
    }
}
