package org.example.person.service.implementations.course;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.person.dto.course.CourseDto;
import org.example.person.dto.course.CourseWithRatingDto;
import org.example.person.exception.connect.ConnectException;
import org.example.person.exception.course.CourseNotFoundException;
import org.example.person.service.api.course.CourseService;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {
    private final CourseRatingService courseRatingService;

    private final WebClient.Builder webClientBuilder;

    @Override
    public CourseDto findCourseById(String authorizationHeader, Long courseId) {
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
                ).onErrorResume(
                        WebClientResponseException.NotFound.class,
                        error -> {
                            log.warn("course by id {} not found", courseId);
                            return Mono.error(
                                    new CourseNotFoundException(courseId)
                            );
                        }
                ).block();
    }

    @Override
    public Map<Long, CourseDto> findCoursesByCourseIdIn(String authorizationHeader, List<Long> courseIds) {
        return webClientBuilder
                .baseUrl("http://course-service/api/v1/course-app/").build()
                .get()
                .uri(uri -> uri
                        .path("courses/")
                        .queryParam("courseIds", courseIds)
                        .build()
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to find courses by course ids");
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Long, CourseDto>>() {
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
    public Boolean existsById(String authorizationHeader, Long courseId) {
        return webClientBuilder
                .baseUrl("http://course-service/api/v1/course-app/").build()
                .get()
                .uri(uri -> uri
                        .path("courses/{courseId}/")
                        .build(courseId)
                )
                .header("Authorization", authorizationHeader)
                .exchangeToMono(
                        r -> {
                            if (r.statusCode().is2xxSuccessful())
                                return Mono.just(true);
                            else if (r.statusCode().equals(HttpStatus.NOT_FOUND))
                                return Mono.just(false);
                            else {
                                log.error("failed to find course by id {}", courseId);
                                return Mono.error(new ConnectException());
                            }
                        }
                )
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(
                        WebClientResponseException.Unauthorized.class,
                        error -> Mono.error(
                                new AccessDeniedException(
                                        "Вам закрыт доступ к данной функциональности бортового компьютера"
                                )
                        )
                ).block();
    }

    @Override
    public List<CourseWithRatingDto> getCoursesRating(String authorizationHeader, List<Long> courseIds) {
        Map<Long, CourseDto> courses = findCoursesByCourseIdIn(authorizationHeader, courseIds);
        Map<Long, Double> courseRatings = courseRatingService.findCourseRatingsByCourseIdIn(
                authorizationHeader, courseIds
        );
        return courses.values()
                .stream()
                .map(c -> new CourseWithRatingDto(
                        c.getCourseId(),
                        c.getTitle(),
                        courseRatings.getOrDefault(c.getCourseId(), 0.0)
                )).collect(Collectors.toList());
    }
}
