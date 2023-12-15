package org.example.progress.repository.implementations;

import lombok.RequiredArgsConstructor;
import org.example.progress.dto.homework.HomeworkDto;
import org.example.progress.exception.classes.connect.ConnectException;
import org.example.progress.repository.HomeworkRepository;
import org.example.progress.utils.AuthorizationHeaderContextHolder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class HomeworkRepositoryImpl implements HomeworkRepository {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderContextHolder authorizationHeaderContextHolder;

    @Override
    public List<HomeworkDto> findHomeworksByCourseThemeIdAndGroupId(Long themeId, Long groupId) {
        return webClientBuilder
                .baseUrl("http://homework-service/api/v1/homework-app/").build()
                .get()
                .uri(uri -> uri
                        .path("themes/{themeId}/groups/{groupId}/homeworks/")
                        .build(themeId, groupId)
                )
                .header("Authorization", authorizationHeaderContextHolder.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(HomeworkDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .collectList()
                .block();
    }

    @Override
    public Map<Long, List<HomeworkDto>> findHomeworksByCourseThemeIdInAndGroupId(List<Long> themeIds, Long groupId) {
        return webClientBuilder
                .baseUrl("http://homework-service/api/v1/homework-app/").build()
                .get()
                .uri(uri -> uri
                        .path("themes/groups/{groupId}/homeworks/")
                        .queryParam("themeIds", themeIds)
                        .build(groupId)
                )
                .header("Authorization", authorizationHeaderContextHolder.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Long, List<HomeworkDto>>>() {
                })
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .blockLast();
    }

    @Override
    public Map<Long, List<HomeworkDto>> findAllCompletedByCourseThemeIdAndGroupIdForExplorers(Long themeId, Long groupId, List<Long> explorerIds) {
        return findAllCompletedByCourseThemeIdInAndGroupIdForExplorers(
                Collections.singletonList(themeId),
                groupId,
                explorerIds
        ).get(themeId);
    }

    @Override
    public Map<Long, Map<Long, List<HomeworkDto>>> findAllCompletedByCourseThemeIdInAndGroupIdForExplorers(List<Long> themeIds, Long groupId, List<Long> explorerIds) {
        return webClientBuilder
                .baseUrl("http://homework-service/api/v1/homework-app/").build()
                .get()
                .uri(uri -> uri
                        .path("themes/groups/{groupId}/homeworks/completed/")
                        .queryParam("themeIds", themeIds)
                        .queryParam("explorerIds", explorerIds)
                        .build(groupId)
                )
                .header("Authorization", authorizationHeaderContextHolder.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Long, Map<Long, List<HomeworkDto>>>>() {
                })
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .blockLast();
    }
}
