package org.example.person.repository.implementations;

import lombok.RequiredArgsConstructor;
import org.example.person.dto.course.CourseDto;
import org.example.person.exception.classes.connect.ConnectException;
import org.example.person.repository.CourseRepository;
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
public class CourseRepositoryImpl implements CourseRepository {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderContextHolder authorizationHeaderContextHolder;

    @Override
    public Map<Long, CourseDto> findCoursesByCourseIdIn(List<Long> courseIds) {
        return webClientBuilder
                .baseUrl("http://course-service/api/v1/course-app/").build()
                .get()
                .uri(uri -> uri
                        .path("courses/")
                        .queryParam("courseIds", courseIds)
                        .build()
                )
                .header("Authorization", authorizationHeaderContextHolder.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Long, CourseDto>>() {
                })
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .blockLast();
    }

    @Override
    public CourseDto getReferenceById(Long courseId) {
        return webClientBuilder
                .baseUrl("http://course-service/api/v1/course-app/").build()
                .get()
                .uri(uri -> uri
                        .path("courses/{courseId}/")
                        .build(courseId)
                )
                .header("Authorization", authorizationHeaderContextHolder.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND) && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToMono(CourseDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .block();
    }

    @Override
    public Boolean existsById(Long courseId) {
        return webClientBuilder
                .baseUrl("http://course-service/api/v1/course-app/").build()
                .get()
                .uri(uri -> uri
                        .path("courses/{courseId}/")
                        .build(courseId)
                )
                .header("Authorization", authorizationHeaderContextHolder.getAuthorizationHeader())
                .exchangeToMono(
                        r -> {
                            if (r.statusCode().is2xxSuccessful())
                                return Mono.just(true);
                            else if (r.statusCode().equals(HttpStatus.NOT_FOUND))
                                return Mono.just(false);
                            else return Mono.error(new ConnectException());
                        }
                )
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .block();
    }
}
