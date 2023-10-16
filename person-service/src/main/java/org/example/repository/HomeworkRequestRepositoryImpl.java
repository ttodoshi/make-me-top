package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.dto.homework.HomeworkRequestDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HomeworkRequestRepositoryImpl implements HomeworkRequestRepository {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderRepository authorizationHeaderRepository;

    @Override
    public List<HomeworkRequestDto> findOpenedHomeworkRequestsByExplorerIdIn(List<Integer> explorerIds) {
        return webClientBuilder
                .baseUrl("http://homework-service/api/v1/homework-app/").build()
                .get()
                .uri(uri -> uri
                        .path("homework-requests/")
                        .queryParam("explorerIds", explorerIds)
                        .build()
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(HomeworkRequestDto.class)
                .timeout(Duration.ofSeconds(5))
                .collectList()
                .block();
    }
}
