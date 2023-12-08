package org.example.courseregistration.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.dto.progress.CourseWithProgressDto;
import org.example.courseregistration.dto.progress.CoursesStateDto;
import org.example.courseregistration.exception.classes.connect.ConnectException;
import org.example.courseregistration.repository.GalaxyRepository;
import org.example.courseregistration.service.CourseProgressService;
import org.example.courseregistration.utils.AuthorizationHeaderContextHolder;
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
    private final AuthorizationHeaderContextHolder authorizationHeaderContextHolder;

    private final GalaxyRepository galaxyRepository;

    @Override
    public boolean isCourseOpenedForAuthenticatedPerson(Long courseId) {
        Long galaxyId = galaxyRepository.findGalaxyBySystemId(courseId).getGalaxyId();
        CoursesStateDto coursesProgress = getCoursesProgress(galaxyId);
        return !coursesProgress.getClosedCourses().contains(courseId);
    }

    @Override
    public boolean isAuthenticatedPersonCurrentlyStudying(Long galaxyId) {
        CoursesStateDto coursesProgress = getCoursesProgress(galaxyId);

        Optional<CourseWithProgressDto> currentCourseProgress = coursesProgress.getStudiedCourses()
                .stream()
                .filter(c -> !c.getProgress().equals(100.0))
                .findAny();
        return currentCourseProgress.isPresent();
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
