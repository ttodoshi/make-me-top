package org.example.person.service.implementations.feedback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.person.exception.connect.ConnectException;
import org.example.person.exception.course.CourseNotFoundException;
import org.example.person.service.api.feedback.CourseRatingService;
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
public class CourseRatingServiceImpl implements CourseRatingService {
    private final WebClient.Builder webClientBuilder;

    @Override
    public Map<Long, Double> findCourseRatingsByCourseIdIn(String authorizationHeader, List<Long> courseIds) {
        return webClientBuilder
                .baseUrl("http://feedback-service/api/v1/feedback-app/").build()
                .get()
                .uri(uri -> uri
                        .path("courses/")
                        .queryParam("courseIds", courseIds)
                        .build()
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND) && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to find course ratings by course ids");
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Long, Double>>() {
                })
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
                        error -> {
                            log.warn("course not found by course ids");
                            return Mono.error(
                                    new CourseNotFoundException()
                            );
                        }
                ).blockLast();
    }
}
