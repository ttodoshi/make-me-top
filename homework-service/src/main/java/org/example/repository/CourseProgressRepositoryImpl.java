package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.dto.progress.CourseWithThemesProgressDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class CourseProgressRepositoryImpl implements CourseProgressRepository {
    private final AuthorizationHeaderRepository authorizationHeaderRepository;

    private final WebClient.Builder webClientBuilder;

    @Override
    public CourseWithThemesProgressDto getCourseProgress(Integer explorerId) {
        return webClientBuilder
                .baseUrl("http://progress-service/api/v1/progress-app/").build()
                .get()
                .uri(uri -> uri
                        .path("explorers/{explorerId}/")
                        .build(explorerId)
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND) && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToMono(CourseWithThemesProgressDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .onErrorResume(WebClientResponseException.NotFound.class, error -> Mono.error(new ExplorerNotFoundException(explorerId)))
                .block();
    }
}
