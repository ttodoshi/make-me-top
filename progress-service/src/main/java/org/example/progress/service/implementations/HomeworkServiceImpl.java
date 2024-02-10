package org.example.progress.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.progress.dto.homework.HomeworkDto;
import org.example.progress.exception.connect.ConnectException;
import org.example.progress.service.HomeworkService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeworkServiceImpl implements HomeworkService {
    private final WebClient.Builder webClientBuilder;

    @Override
    public List<HomeworkDto> findHomeworksByCourseThemeIdAndGroupId(String authorizationHeader, Long themeId, Long groupId) {
        return webClientBuilder
                .baseUrl("http://homework-service/api/v1/homework-app/").build()
                .get()
                .uri(uri -> uri
                        .path("themes/{themeId}/groups/{groupId}/homeworks/")
                        .build(themeId, groupId)
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to find homewordk by theme id {} and group id {}", themeId, groupId);
                    throw new ConnectException();
                })
                .bodyToFlux(HomeworkDto.class)
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

    @Override
    public Map<Long, List<HomeworkDto>> findHomeworksByCourseThemeIdInAndGroupId(String authorizationHeader, List<Long> themeIds, Long groupId) {
        return webClientBuilder
                .baseUrl("http://homework-service/api/v1/homework-app/").build()
                .get()
                .uri(uri -> uri
                        .path("themes/groups/{groupId}/homeworks/")
                        .queryParam("themeIds", themeIds)
                        .build(groupId)
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to find homework by theme ids and group id {}", groupId);
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Long, List<HomeworkDto>>>() {
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
    public Map<Long, List<HomeworkDto>> findAllCompletedByCourseThemeIdAndGroupIdForExplorers(String authorizationHeader, Long themeId, Long groupId, List<Long> explorerIds) {
        return findAllCompletedByCourseThemeIdInAndGroupIdForExplorers(
                authorizationHeader, Collections.singletonList(themeId), groupId, explorerIds
        ).get(themeId);
    }

    @Override
    public Map<Long, Map<Long, List<HomeworkDto>>> findAllCompletedByCourseThemeIdInAndGroupIdForExplorers(String authorizationHeader, List<Long> themeIds, Long groupId, List<Long> explorerIds) {
        return webClientBuilder
                .baseUrl("http://homework-service/api/v1/homework-app/").build()
                .get()
                .uri(uri -> uri
                        .path("themes/groups/{groupId}/homeworks/completed/")
                        .queryParam("themeIds", themeIds)
                        .queryParam("explorerIds", explorerIds)
                        .build(groupId)
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to find completed homeworks by theme ids and group id {} for explorers", groupId);
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Long, Map<Long, List<HomeworkDto>>>>() {
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
}
