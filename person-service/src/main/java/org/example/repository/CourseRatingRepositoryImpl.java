package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CourseRatingRepositoryImpl implements CourseRatingRepository {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderRepository authorizationHeaderRepository;

    @Override
    public Map<Integer, Double> findCourseRatingsByCourseIdIn(List<Integer> courseIds) {
        return webClientBuilder
                .baseUrl("http://feedback-service/api/v1/feedback-app/").build()
                .get()
                .uri(uri -> uri
                        .path("courses/")
                        .queryParam("courseIds", courseIds)
                        .build()
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Integer, Double>>() {
                })
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.NotFound.class, error -> Mono.error(new CourseNotFoundException()))
                .blockLast();
    }
}
