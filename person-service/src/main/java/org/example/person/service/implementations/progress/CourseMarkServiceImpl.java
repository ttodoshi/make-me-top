package org.example.person.service.implementations.progress;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.person.dto.mark.CourseMarkDto;
import org.example.person.exception.connect.ConnectException;
import org.example.person.service.api.progress.CourseMarkService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
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
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND) && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to find course mark by explorer id {}", explorerId);
                    throw new ConnectException();
                })
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
