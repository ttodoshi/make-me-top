package org.example.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.dto.progress.CourseWithProgressDto;
import org.example.dto.progress.CoursesStateDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.repository.AuthorizationHeaderRepository;
import org.example.repository.GalaxyRepository;
import org.example.service.CourseProgressService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CourseProgressServiceImpl implements CourseProgressService {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderRepository authorizationHeaderRepository;

    private final GalaxyRepository galaxyRepository;

    @Override
    public boolean isCourseOpenedForAuthenticatedPerson(Integer courseId) {
        Integer galaxyId = galaxyRepository.getGalaxyBySystemId(courseId).getGalaxyId();
        CoursesStateDto coursesProgress = getCoursesProgress(galaxyId);
        return !coursesProgress.getClosedCourses().contains(courseId);
    }

    @Override
    public boolean isAuthenticatedPersonCurrentlyStudying(Integer galaxyId) {
        CoursesStateDto coursesProgress = getCoursesProgress(galaxyId);
        Optional<CourseWithProgressDto> currentCourseProgress = coursesProgress.getStudiedCourses()
                .stream()
                .filter(c -> !c.getProgress().equals(100.0))
                .findAny();
        return currentCourseProgress.isPresent();
    }

    private CoursesStateDto getCoursesProgress(Integer galaxyId) {
        return webClientBuilder
                .baseUrl("http://progress-service/api/v1/progress-app/").build()
                .get()
                .uri(uri -> uri
                        .path("galaxies/{galaxyId}/")
                        .build(galaxyId)
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
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
