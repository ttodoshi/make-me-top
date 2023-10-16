package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.dto.feedback.KeeperFeedbackDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class KeeperFeedbackRepositoryImpl implements KeeperFeedbackRepository {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderRepository authorizationHeaderRepository;

    @Override
    public List<KeeperFeedbackDto> findKeeperFeedbacksByExplorerIdIn(List<Integer> explorerIds) {
        return webClientBuilder
                .baseUrl("http://feedback-service/api/v1/feedback-app/").build()
                .get()
                .uri(uri -> uri
                        .path("keeper-feedbacks/")
                        .queryParam("explorerIds", explorerIds)
                        .build()
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(KeeperFeedbackDto.class)
                .timeout(Duration.ofSeconds(5))
                .collectList()
                .block();
    }
}
