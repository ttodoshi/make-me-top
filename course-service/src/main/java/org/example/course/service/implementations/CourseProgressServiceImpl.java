package org.example.course.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.course.dto.progress.CourseWithThemesProgressDto;
import org.example.course.exception.connect.ConnectException;
import org.example.course.exception.explorer.ExplorerNotFoundException;
import org.example.course.service.CourseProgressService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseProgressServiceImpl implements CourseProgressService {
    private final WebClient.Builder webClientBuilder;

    @Override
    public CourseWithThemesProgressDto getCourseProgress(String authorizationHeader, Long explorerId) {
        return webClientBuilder
                .baseUrl("http://progress-service/api/v1/progress-app/").build()
                .get()
                .uri(uri -> uri
                        .path("explorers/{explorerId}/")
                        .build(explorerId)
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND) && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to get course progress by explorer id {}", explorerId);
                    throw new ConnectException();
                })
                .bodyToMono(CourseWithThemesProgressDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(
                        WebClientResponseException.Unauthorized.class,
                        error -> Mono.error(
                                new AccessDeniedException(
                                        "Вам закрыт доступ к данной функциональности бортового компьютера"
                                )
                        )
                ).onErrorResume(
                        WebClientResponseException.NotFound.class,
                        error -> Mono.error(
                                new ExplorerNotFoundException(explorerId)
                        )
                ).block();
    }
}
