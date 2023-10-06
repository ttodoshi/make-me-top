package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.dto.feedback.ExplorerFeedbackDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExplorerFeedbackRepositoryImpl implements ExplorerFeedbackRepository {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderRepository authorizationHeaderRepository;

    @Override
    public List<ExplorerFeedbackDto> findExplorerFeedbacksByKeeperIdIn(List<Integer> keeperIds) {
        return webClientBuilder
                .baseUrl("http://feedback-service/api/v1/feedback-app/").build()
                .get()
                .uri(uri -> uri
                        .path("explorer-feedback/")
                        .queryParam("keeperIds", keeperIds)
                        .build()
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(ExplorerFeedbackDto.class)
                .timeout(Duration.ofSeconds(5))
                .collectList()
                .block();
    }
}
