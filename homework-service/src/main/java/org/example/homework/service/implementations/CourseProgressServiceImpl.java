package org.example.homework.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.homework.dto.group.CurrentKeeperGroupDto;
import org.example.homework.dto.progress.CourseWithThemesProgressDto;
import org.example.homework.exception.connect.ConnectException;
import org.example.homework.exception.explorer.ExplorerNotFoundException;
import org.example.homework.service.CourseProgressService;
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
public class CourseProgressServiceImpl implements CourseProgressService {
    private final WebClient.Builder webClientBuilder;

    @Override
    public CourseWithThemesProgressDto getCourseProgress(String authorizationHeader, Long explorerId) {
        return webClientBuilder
                .baseUrl("http://progress-service/api/v1/progress-app/").build()
                .get()
                .uri(uri -> uri
                        .path("explorers/{explorerId}/")
                        .build(explorerId)
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND) && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to get explorer progress");
                    throw new ConnectException();
                })
                .bodyToMono(CourseWithThemesProgressDto.class)
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
                            log.warn("explorer by id {} not found", explorerId);
                            return Mono.error(
                                    new ExplorerNotFoundException(explorerId)
                            );
                        }
                ).block();
    }

    @Override
    public Optional<CurrentKeeperGroupDto> getCurrentGroup(String authorizationHeader) {
        return webClientBuilder
                .baseUrl("http://person-service/api/v1/person-app/").build()
                .get()
                .uri("groups/current/")
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND) && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to get current keeper group");
                    throw new ConnectException();
                })
                .bodyToMono(CurrentKeeperGroupDto.class)
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
