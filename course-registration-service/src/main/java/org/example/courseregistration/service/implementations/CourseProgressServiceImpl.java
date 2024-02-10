package org.example.courseregistration.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.courseregistration.dto.progress.CoursesStateDto;
import org.example.courseregistration.exception.connect.ConnectException;
import org.example.courseregistration.service.CourseProgressService;
import org.example.courseregistration.service.ExplorerService;
import org.example.courseregistration.service.GalaxyService;
import org.example.grpc.ExplorersService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseProgressServiceImpl implements CourseProgressService {
    private final WebClient.Builder webClientBuilder;

    private final GalaxyService galaxyService;
    private final ExplorerService explorerService;

    @Override
    public boolean isCourseOpenedForAuthenticatedPerson(String authorizationHeader, Long courseId) {
        CoursesStateDto coursesProgress = getCoursesProgress(
                authorizationHeader,
                galaxyService
                        .findGalaxyBySystemId(authorizationHeader, courseId)
                        .getGalaxyId()
        );
        return !coursesProgress.getClosedCourses().contains(courseId);
    }

    @Override
    public boolean isAuthenticatedPersonCurrentlyStudying(String authorizationHeader, Long authenticatedPersonId) {
        List<ExplorersService.Explorer> personExplorers = explorerService.findExplorersByPersonId(
                authorizationHeader, authenticatedPersonId
        );

        Set<Long> explorersWithFinalAssessment = getExplorersWithFinalAssessment(
                authorizationHeader, personExplorers.stream().map(ExplorersService.Explorer::getExplorerId).collect(Collectors.toList())
        );
        return personExplorers.stream()
                .anyMatch(e -> !explorersWithFinalAssessment.contains(e.getExplorerId()));
    }

    @Override
    public Set<Long> getExplorersWithFinalAssessment(String authorizationHeader, List<Long> explorerIds) {
        return webClientBuilder
                .baseUrl("http://progress-service/api/v1/progress-app/").build()
                .get()
                .uri(uri -> uri
                        .path("explorers/completed/")
                        .queryParam("explorerIds", explorerIds)
                        .build()
                ).header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to get explorers with final assessment");
                    throw new ConnectException();
                })
                .bodyToFlux(Long.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(
                        WebClientResponseException.Unauthorized.class,
                        error -> Mono.error(
                                new AccessDeniedException(
                                        "Вам закрыт доступ к данной функциональности бортового компьютера"
                                )
                        )
                ).collect(Collectors.toSet())
                .block();
    }

    private CoursesStateDto getCoursesProgress(String authorizationHeader, Long galaxyId) {
        return webClientBuilder
                .baseUrl("http://progress-service/api/v1/progress-app/").build()
                .get()
                .uri(uri -> uri
                        .path("galaxies/{galaxyId}/")
                        .build(galaxyId)
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to get courses progress");
                    throw new ConnectException();
                })
                .bodyToMono(CoursesStateDto.class)
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
}
