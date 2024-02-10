package org.example.progress.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.progress.dto.course.CourseDto;
import org.example.progress.exception.connect.ConnectException;
import org.example.progress.exception.course.CourseNotFoundException;
import org.example.progress.service.CourseService;
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
public class CourseServiceImpl implements CourseService {
    private final WebClient.Builder webClientBuilder;

    @Override
    public CourseDto findById(String authorizationHeader, Long courseId) {
        return webClientBuilder
                .baseUrl("http://course-service/api/v1/course-app/").build()
                .get()
                .uri(uri -> uri
                        .path("courses/{courseId}/")
                        .build(courseId)
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND) && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to find course by id {}", courseId);
                    throw new ConnectException();
                })
                .bodyToMono(CourseDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(
                        WebClientResponseException.Unauthorized.class,
                        error -> Mono.error(
                                new AccessDeniedException(
                                        "Вам закрыт доступ к данной функциональности бортового компьютера"
                                )
                        )
                )
                .onErrorResume(
                        WebClientResponseException.NotFound.class,
                        error -> {
                            log.warn("course by id {} not found", courseId);
                            return Mono.error(
                                    new CourseNotFoundException(courseId)
                            );
                        }
                ).block();
    }
}
