package org.example.person.service.implementations.feedback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.person.dto.feedback.KeeperFeedbackDto;
import org.example.person.exception.connect.ConnectException;
import org.example.person.service.api.feedback.KeeperFeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeeperFeedbackServiceImpl implements KeeperFeedbackService {
    private final WebClient.Builder webClientBuilder;

    @Override
    public List<KeeperFeedbackDto> findKeeperFeedbacksByIdIn(String authorizationHeader, List<Long> feedbackIds) {
        return webClientBuilder
                .baseUrl("http://feedback-service/api/v1/feedback-app/").build()
                .get()
                .uri(uri -> uri
                        .path("keeper-feedbacks/")
                        .queryParam("feedbackIds", feedbackIds)
                        .build()
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.warn("failed to find keeper feedback by feedback ids");
                    throw new ConnectException();
                })
                .bodyToFlux(KeeperFeedbackDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(
                        WebClientResponseException.Unauthorized.class,
                        error -> Mono.error(
                                new AccessDeniedException(
                                        "Вам закрыт доступ к данной функциональности бортового компьютера"
                                )
                        )
                ).collectList()
                .block();
    }
}
