package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.dto.starsystem.StarSystemDTO;
import org.example.dto.starsystem.StarSystemWithDependenciesGetResponse;
import org.example.dto.starsystem.SystemDependencyModel;
import org.example.dto.systemprogress.PlanetCompletionDTO;
import org.example.dto.systemprogress.StarSystemsState;
import org.example.dto.systemprogress.SystemWithPlanetsProgress;
import org.example.dto.systemprogress.SystemWithProgress;
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
import org.example.repository.PlanetProgressRepository;
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
public class SystemProgressService {
    private final PlanetProgressRepository planetProgressRepository;
    private final ExplorerRepository explorerRepository;
    private final CourseRepository courseRepository;
    private final CourseThemeRepository courseThemeRepository;

    @Setter
    private String token;
    @Value("${app_galaxy_url}")
    private String GALAXY_APP_URL;

    public StarSystemsState getSystemsProgressForCurrentUser(Integer galaxyId) {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Set<Integer> openedSystems = new LinkedHashSet<>();
        Set<SystemWithProgress> studiedSystems = new LinkedHashSet<>();
        Set<Integer> closedSystems = new LinkedHashSet<>();
        for (StarSystemDTO system : getSystemsByGalaxyId(galaxyId)) {
            Optional<Explorer> explorerOptional = explorerRepository.findExplorerByPersonIdAndCourseId(
                    authenticatedPerson.getPersonId(), system.getSystemId());
            if (explorerOptional.isPresent()) {
                Explorer explorer = explorerOptional.get();
                studiedSystems.add(new SystemWithProgress(explorer.getCourseId(),
                        planetProgressRepository.getSystemProgress(
                                explorer.getExplorerId(), explorer.getCourseId())));
            } else {
                if (hasUncompletedParents(authenticatedPerson.getPersonId(), system.getSystemId()))
                    closedSystems.add(system.getSystemId());
                else
                    openedSystems.add(system.getSystemId());
            }
        }
        return StarSystemsState.builder()
                .personId(authenticatedPerson.getPersonId())
                .firstName(authenticatedPerson.getFirstName())
                .lastName(authenticatedPerson.getLastName())
                .patronymic(authenticatedPerson.getPatronymic())
                .openedSystems(openedSystems)
                .studiedSystems(studiedSystems)
                .closedSystems(closedSystems)
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

    public SystemWithPlanetsProgress getPlanetsProgressBySystemId(Integer systemId) {
        Integer authenticatedPersonId = getAuthenticatedPersonId();
        Course course = courseRepository.findById(systemId).orElseThrow(
                () -> new CourseNotFoundException(systemId));
        Explorer explorer = explorerRepository.findExplorerByPersonIdAndCourseId(authenticatedPersonId, systemId)
                .orElseThrow(() -> new ExplorerNotFoundException(systemId));
        List<PlanetCompletionDTO> planetsCompletion = new LinkedList<>();
        for (CourseTheme ct : courseThemeRepository.findCourseThemesByCourseIdOrderByCourseThemeNumberAsc(systemId)) {
            Boolean planetCompleted = planetProgressRepository.findCourseThemeProgressByExplorerIdAndCourseThemeId(explorer.getExplorerId(), ct.getCourseThemeId()).isPresent();
            planetsCompletion.add(
                    new PlanetCompletionDTO(ct.getCourseThemeId(), ct.getTitle(), planetCompleted)
            );
        }
        return SystemWithPlanetsProgress.builder()
                .courseId(systemId)
                .title(course.getTitle())
                .planetsWithProgress(planetsCompletion)
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
            if (explorer.isEmpty() || planetProgressRepository.getSystemProgress(
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
