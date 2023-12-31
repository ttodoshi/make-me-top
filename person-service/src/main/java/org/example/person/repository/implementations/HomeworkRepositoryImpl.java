package org.example.person.repository.implementations;

import lombok.RequiredArgsConstructor;
import org.example.person.dto.homework.GetHomeworkWithMarkDto;
import org.example.person.dto.homework.HomeworkDto;
import org.example.person.exception.classes.connect.ConnectException;
import org.example.person.repository.HomeworkRepository;
import org.example.person.utils.AuthorizationHeaderContextHolder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class HomeworkRepositoryImpl implements HomeworkRepository {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderContextHolder authorizationHeaderContextHolder;

    @Override
    public Map<Long, HomeworkDto> findHomeworksByHomeworkIdIn(List<Long> homeworkIds) {
        return webClientBuilder
                .baseUrl("http://homework-service/api/v1/homework-app/").build()
                .get()
                .uri(uri -> uri
                        .path("homeworks/")
                        .queryParam("homeworkIds", homeworkIds)
                        .build()
                )
                .header("Authorization", authorizationHeaderContextHolder.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Long, HomeworkDto>>() {
                })
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .blockLast();
    }

    @Override
    public List<GetHomeworkWithMarkDto> findHomeworksByCourseThemeId(Long themeId) {
        return webClientBuilder
                .baseUrl("http://homework-service/api/v1/homework-app/").build()
                .get()
                .uri(uri -> uri
                        .path("themes/{themeId}/homeworks/")
                        .build(themeId)
                )
                .header("Authorization", authorizationHeaderContextHolder.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(GetHomeworkWithMarkDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .collectList()
                .block();
    }
}
