package org.example.person.service.implementations.homework;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.person.dto.homework.HomeworkRequestDto;
import org.example.person.exception.connect.ConnectException;
import org.example.person.service.api.homework.HomeworkRequestService;
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
public class HomeworkRequestServiceImpl implements HomeworkRequestService {
    private final WebClient.Builder webClientBuilder;

    @Override
    public List<HomeworkRequestDto> findOpenedHomeworkRequestsByExplorerIdIn(String authorizationHeader, List<Long> explorerIds) {
        return webClientBuilder
                .baseUrl("http://homework-service/api/v1/homework-app/").build()
                .get()
                .uri(uri -> uri
                        .path("homework-requests/")
                        .queryParam("explorerIds", explorerIds)
                        .build()
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to find opened homework requests by explorer ids");
                    throw new ConnectException();
                })
                .bodyToFlux(HomeworkRequestDto.class)
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
