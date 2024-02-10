package org.example.course.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.course.dto.mark.CourseMarkDto;
import org.example.course.service.CourseMarkService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseMarkServiceImpl implements CourseMarkService {
    private final WebClient.Builder webClientBuilder;

    @Override
    public Optional<CourseMarkDto> findById(String authorizationHeader, Long explorerId) {
        return webClientBuilder
                .baseUrl("http://progress-service/api/v1/progress-app/").build()
                .get()
                .uri(uri -> uri
                        .path("explorers/{explorerId}/marks/")
                        .build(explorerId)
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .bodyToMono(CourseMarkDto.class)
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
                        error -> Mono.empty()
                ).blockOptional();
    }
}
