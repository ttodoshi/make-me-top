package org.example.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.dto.course.GetCourseDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.repository.AuthorizationHeaderRepository;
import org.example.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final AuthorizationHeaderRepository authorizationHeaderRepository;
    private final WebClient.Builder webClientBuilder;

    @Override
    public GetCourseDto getCourseById(Integer courseId) {
        return webClientBuilder
                .baseUrl("http://course-service/api/v1/course-app/").build()
                .get()
                .uri(uri -> uri
                        .path("courses/{courseId}/")
                        .queryParam("detailed", true)
                        .build(courseId)
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToMono(GetCourseDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .block();
    }
}
