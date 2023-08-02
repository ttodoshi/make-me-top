package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.dto.starsystem.StarSystemDTO;
import org.example.dto.starsystem.StarSystemWithDependenciesGetResponse;
import org.example.dto.starsystem.SystemDependencyModel;
import org.example.dto.courseprogress.CourseThemeCompletionDTO;
import org.example.dto.courseprogress.CoursesState;
import org.example.dto.courseprogress.CourseWithThemesProgress;
import org.example.dto.courseprogress.CourseWithProgress;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.model.Explorer;
import org.example.model.Person;
import org.example.model.course.Course;
import org.example.model.course.CourseTheme;
import org.example.repository.CourseRepository;
import org.example.repository.CourseThemeRepository;
import org.example.repository.ExplorerRepository;
import org.example.repository.CourseThemeCompletionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseProgressService {
    private final CourseThemeCompletionRepository courseThemeCompletionRepository;
    private final ExplorerRepository explorerRepository;
    private final CourseRepository courseRepository;
    private final CourseThemeRepository courseThemeRepository;

    @Setter
    private String token;
    @Value("${galaxy_app_url}")
    private String GALAXY_APP_URL;

    public CoursesState getCoursesProgressForCurrentUser(Integer galaxyId) {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Set<Integer> openedCourses = new LinkedHashSet<>();
        Set<CourseWithProgress> studiedCourses = new LinkedHashSet<>();
        Set<Integer> closedCourses = new LinkedHashSet<>();
        for (StarSystemDTO system : getSystemsByGalaxyId(galaxyId)) {
            Optional<Explorer> explorerOptional = explorerRepository.findExplorerByPersonIdAndCourseId(
                    authenticatedPerson.getPersonId(), system.getSystemId());
            if (explorerOptional.isPresent()) {
                Explorer explorer = explorerOptional.get();
                studiedCourses.add(new CourseWithProgress(explorer.getCourseId(),
                        courseThemeCompletionRepository.getCourseProgress(
                                explorer.getExplorerId(), explorer.getCourseId())));
            } else {
                if (hasUncompletedParents(authenticatedPerson.getPersonId(), system.getSystemId()))
                    closedCourses.add(system.getSystemId());
                else
                    openedCourses.add(system.getSystemId());
            }
        }
        return CoursesState.builder()
                .personId(authenticatedPerson.getPersonId())
                .firstName(authenticatedPerson.getFirstName())
                .lastName(authenticatedPerson.getLastName())
                .patronymic(authenticatedPerson.getPatronymic())
                .openedCourses(openedCourses)
                .studiedCourses(studiedCourses)
                .closedCourses(closedCourses)
                .build();
    }

    private StarSystemDTO[] getSystemsByGalaxyId(Integer galaxyId) {
        WebClient webClient = WebClient.create(GALAXY_APP_URL);
        return webClient.get()
                .uri("/galaxy/" + galaxyId + "/system/")
                .header("Authorization", token)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
                    throw new GalaxyNotFoundException(galaxyId);
                })
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToMono(StarSystemDTO[].class)
                .block(Duration.ofSeconds(5));
    }

    public CourseWithThemesProgress getThemesProgressByCourseId(Integer systemId) {
        Integer authenticatedPersonId = getAuthenticatedPersonId();
        Course course = courseRepository.findById(systemId).orElseThrow(
                () -> new CourseNotFoundException(systemId));
        Explorer explorer = explorerRepository.findExplorerByPersonIdAndCourseId(authenticatedPersonId, systemId)
                .orElseThrow(() -> new ExplorerNotFoundException(systemId));
        List<CourseThemeCompletionDTO> planetsCompletion = new LinkedList<>();
        for (CourseTheme ct : courseThemeRepository.findCourseThemesByCourseIdOrderByCourseThemeNumberAsc(systemId)) {
            Boolean themeCompleted = courseThemeCompletionRepository.findCourseThemeProgressByExplorerIdAndCourseThemeId(explorer.getExplorerId(), ct.getCourseThemeId()).isPresent();
            planetsCompletion.add(
                    new CourseThemeCompletionDTO(ct.getCourseThemeId(), ct.getTitle(), themeCompleted)
            );
        }
        return CourseWithThemesProgress.builder()
                .courseId(systemId)
                .title(course.getTitle())
                .themesWithProgress(planetsCompletion)
                .build();
    }

    private Integer getAuthenticatedPersonId() {
        final Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }

    public boolean hasUncompletedParents(Integer personId, Integer systemId) {
        boolean parentsUncompleted = false;
        StarSystemWithDependenciesGetResponse systemWithDependencies = getStarSystemWithDependencies(systemId);
        if (systemWithDependencies == null)
            return false;
        for (SystemDependencyModel system : getParentDependencies(systemWithDependencies)) {
            Optional<Explorer> explorer = explorerRepository.findExplorerByPersonIdAndCourseId(personId, system.getSystemId());
            if (explorer.isEmpty() || courseThemeCompletionRepository.getCourseProgress(
                    explorer.get().getExplorerId(), explorer.get().getCourseId()) < 100) {
                parentsUncompleted = true;
            } else if (system.getIsAlternative())
                return false;
        }
        return parentsUncompleted;
    }

    private StarSystemWithDependenciesGetResponse getStarSystemWithDependencies(Integer systemId) {
        WebClient webClient = WebClient.create(GALAXY_APP_URL);
        return webClient.get()
                .uri("/system/" + systemId + "?withDependencies=true")
                .header("Authorization", token)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
                    throw new CourseNotFoundException(systemId);
                })
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToMono(StarSystemWithDependenciesGetResponse.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }

    private List<SystemDependencyModel> getParentDependencies(StarSystemWithDependenciesGetResponse systemWithDependencies) {
        return systemWithDependencies.getDependencyList()
                .stream()
                .filter(s -> s.getType().equals("parent"))
                .collect(Collectors.toList());
    }
}
