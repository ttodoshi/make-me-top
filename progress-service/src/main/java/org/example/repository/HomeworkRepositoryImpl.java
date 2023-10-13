package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.dto.homework.HomeworkDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HomeworkRepositoryImpl implements HomeworkRepository {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderRepository authorizationHeaderRepository;

    @Override
    public List<HomeworkDto> findHomeworksByCourseThemeIdAndGroupId(Integer themeId, Integer groupId) {
        return webClientBuilder
                .baseUrl("http://homework-service/api/v1/homework-app/").build()
                .get()
                .uri(uri -> uri
                        .path("theme/{themeId}/group/{groupId}/homework/")
                        .build(themeId, groupId)
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(HomeworkDto.class)
                .timeout(Duration.ofSeconds(5))
                .collectList()
                .block();
    }

    @Override
    public List<HomeworkDto> findAllCompletedByCourseThemeIdAndGroupIdForExplorer(Integer themeId, Integer groupId, Integer explorerId) {
        return webClientBuilder
                .baseUrl("http://homework-service/api/v1/homework-app/").build()
                .get()
                .uri(uri -> uri
                        .path("theme/{themeId}/group/{groupId}/homework/completed/")
                        .queryParam("explorerId", explorerId)
                        .build(themeId, groupId)
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(HomeworkDto.class)
                .timeout(Duration.ofSeconds(5))
                .collectList()
                .block();
    }
}
