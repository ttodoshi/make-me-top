package org.example.courseregistration.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.dto.progress.CoursesStateDto;
import org.example.courseregistration.exception.classes.connect.ConnectException;
import org.example.courseregistration.repository.ExplorerRepository;
import org.example.courseregistration.repository.GalaxyRepository;
import org.example.courseregistration.service.CourseProgressService;
import org.example.courseregistration.service.PersonService;
import org.example.courseregistration.utils.AuthorizationHeaderContextHolder;
import org.example.grpc.ExplorersService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CourseProgressServiceImpl implements CourseProgressService {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderContextHolder authorizationHeaderContextHolder;

    private final PersonService personService;

    private final GalaxyRepository galaxyRepository;
    private final ExplorerRepository explorerRepository;

    @Override
    public boolean isCourseOpenedForAuthenticatedPerson(Long courseId) {
        Long galaxyId = galaxyRepository.findGalaxyBySystemId(courseId).getGalaxyId();
        CoursesStateDto coursesProgress = getCoursesProgress(galaxyId);
        return !coursesProgress.getClosedCourses().contains(courseId);
    }

    @Override
    public boolean isAuthenticatedPersonCurrentlyStudying() {
        List<ExplorersService.Explorer> personExplorers = explorerRepository.findExplorersByPersonId(
                personService.getAuthenticatedPersonId()
        );

        Set<Long> explorersWithFinalAssessment = getExplorersWithFinalAssessment(
                personExplorers.stream().map(ExplorersService.Explorer::getExplorerId).collect(Collectors.toList())
        );
        return personExplorers.stream()
                .anyMatch(e -> !explorersWithFinalAssessment.contains(e.getExplorerId()));
    }

    private Set<Long> getExplorersWithFinalAssessment(List<Long> explorerIds) {
        return webClientBuilder
                .baseUrl("http://progress-service/api/v1/progress-app/").build()
                .get()
                .uri(uri -> uri
                        .path("explorers/completed/")
                        .queryParam("explorerIds", explorerIds)
                        .build()
                ).header("Authorization", authorizationHeaderContextHolder.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(Long.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .collect(Collectors.toSet())
                .block();
    }

    private CoursesStateDto getCoursesProgress(Long galaxyId) {
        return webClientBuilder
                .baseUrl("http://progress-service/api/v1/progress-app/").build()
                .get()
                .uri(uri -> uri
                        .path("galaxies/{galaxyId}/")
                        .build(galaxyId)
                )
                .header("Authorization", authorizationHeaderContextHolder.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToMono(CoursesStateDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .block();
    }
}
