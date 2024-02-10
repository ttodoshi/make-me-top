package org.example.person.service.implementations.feedback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.person.dto.feedback.offer.ExplorerFeedbackOfferDto;
import org.example.person.exception.connect.ConnectException;
import org.example.person.service.api.feedback.ExplorerFeedbackOfferService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExplorerFeedbackOfferServiceImpl implements ExplorerFeedbackOfferService {
    private final WebClient.Builder webClientBuilder;

    @Override
    public Map<Long, ExplorerFeedbackOfferDto> findExplorerFeedbackOffersByKeeperIdIn(String authorizationHeader, List<Long> keeperIds) {
        return webClientBuilder
                .baseUrl("http://feedback-service/api/v1/feedback-app/").build()
                .get()
                .uri(uri -> uri
                        .path("explorer-feedbacks/offers/")
                        .queryParam("keeperIds", keeperIds)
                        .build()
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to find explorer feedback offers by keeper ids");
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Long, ExplorerFeedbackOfferDto>>() {
                })
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(
                        WebClientResponseException.Unauthorized.class,
                        error -> Mono.error(
                                new AccessDeniedException(
                                        "Вам закрыт доступ к данной функциональности бортового компьютера"
                                )
                        )
                ).blockLast();
    }

    @Override
    public List<ExplorerFeedbackOfferDto> findExplorerFeedbackOffersByExplorerIdInAndOfferValidIsTrue(String authorizationHeader, List<Long> explorerIds) {
        return webClientBuilder
                .baseUrl("http://feedback-service/api/v1/feedback-app/").build()
                .get()
                .uri(uri -> uri
                        .path("explorer-feedbacks/offers/valid")
                        .queryParam("explorerIds", explorerIds)
                        .build()
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to find explorer feedback offers by explorer ids and valid");
                    throw new ConnectException();
                })
                .bodyToFlux(ExplorerFeedbackOfferDto.class)
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
