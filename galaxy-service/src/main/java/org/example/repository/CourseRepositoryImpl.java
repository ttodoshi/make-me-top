package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.dto.course.CourseGetResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class CourseRepositoryImpl implements CourseRepository {
    private final AuthorizationHeaderRepository authorizationHeaderRepository;
    @Value("${course_app_url}")
    private String COURSE_APP_URL;

    @Override
    public CourseGetResponse getCourseById(Integer courseId) {
        WebClient webClient = WebClient.create(COURSE_APP_URL);
        return webClient.get()
                .uri("course/" + courseId + "/")
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .bodyToMono(CourseGetResponse.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }
}
