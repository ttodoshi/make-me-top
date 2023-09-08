package org.example.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.dto.courseprogress.CourseWithThemesProgressDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.repository.custom.AuthorizationHeaderRepository;
import org.example.service.CourseProgressService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class CourseProgressServiceImpl implements CourseProgressService {
    private final AuthorizationHeaderRepository authorizationHeaderRepository;

    private final WebClient.Builder webClientBuilder;

    @Override
    public CourseWithThemesProgressDto getCourseProgress(Integer courseId) {
        return webClientBuilder
                .baseUrl("http://explorer-personal-cabinet-service/explorer-cabinet/").build()
                .get()
                .uri("course/" + courseId)
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
                    throw new CourseNotFoundException(courseId);
                })
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToMono(CourseWithThemesProgressDto.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }
}
