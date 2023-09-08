package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.dto.course.GetCourseDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class CourseRepositoryImpl implements CourseRepository {
    private final AuthorizationHeaderRepository authorizationHeaderRepository;
    private final WebClient.Builder webClientBuilder;

    @Override
    public GetCourseDto getCourseById(Integer courseId) {
        return webClientBuilder
                .baseUrl("http://course-service/course-app/").build()
                .get()
                .uri("course/" + courseId + "/")
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToMono(GetCourseDto.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }
}
