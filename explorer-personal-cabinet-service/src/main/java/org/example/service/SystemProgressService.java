package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.dto.starsystem.*;
import org.example.dto.systemprogress.PlanetCompletionDTO;
import org.example.dto.systemprogress.SystemWithPlanetsProgress;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.model.Explorer;
import org.example.model.Person;
import org.example.model.course.Course;
import org.example.model.course.CourseTheme;
import org.example.model.progress.CourseThemeCompletion;
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

    public StarSystemsForUser getSystemsProgressForCurrentUser(Integer galaxyId) {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Set<Integer> openedSystems = new HashSet<>();
        Set<SystemWithProgress> studiedSystems = new HashSet<>();
        Set<Integer> closedSystems = new HashSet<>();
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
        return StarSystemsForUser.builder()
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
        Course course = courseRepository.findById(systemId).orElseThrow(() -> new CourseNotFoundException(systemId));
        Optional<Explorer> explorerOptional = explorerRepository.findExplorerByPersonIdAndCourseId(authenticatedPersonId, systemId);
        if (explorerOptional.isEmpty())
            throw new ExplorerNotFoundException();
        Explorer explorer = explorerOptional.get();
        SystemWithPlanetsProgress systemWithPlanetsProgress = new SystemWithPlanetsProgress();
        systemWithPlanetsProgress.setCourseId(course.getCourseId());
        systemWithPlanetsProgress.setTitle(course.getTitle());
        List<PlanetCompletionDTO> planetCompletionDTOS = new LinkedList<>();
        for (CourseTheme ct : courseThemeRepository.findCourseThemesByCourseIdOrderByCourseThemeNumberAsc(systemId)) {
            Optional<CourseThemeCompletion> planetProgress = planetProgressRepository.findCourseThemeProgressByExplorerIdAndCourseThemeId(explorer.getExplorerId(), ct.getCourseThemeId());
            if (planetProgress.isPresent())
                planetCompletionDTOS.add(
                        new PlanetCompletionDTO(ct.getCourseThemeId(), ct.getTitle(), planetProgress.get().getCompleted())
                );
            else {
                planetCompletionDTOS.add(
                        new PlanetCompletionDTO(ct.getCourseThemeId(), ct.getTitle(), false)
                );
            }
        }
        systemWithPlanetsProgress.setPlanetsWithProgress(planetCompletionDTOS);
        return systemWithPlanetsProgress;
    }

    private Integer getAuthenticatedPersonId() {
        final Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }

    public boolean hasUncompletedParents(Integer personId, Integer systemId) {
        boolean parentsUncompleted = false;
        GetStarSystemWithDependencies systemWithDependencies = getStarSystemWithDependencies(systemId);
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

    private GetStarSystemWithDependencies getStarSystemWithDependencies(Integer systemId) {
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
                .bodyToMono(GetStarSystemWithDependencies.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }

    private List<SystemDependencyModel> getParentDependencies(GetStarSystemWithDependencies systemWithDependencies) {
        return systemWithDependencies.getDependencyList()
                .stream()
                .filter(s -> s.getType().equals("parent"))
                .collect(Collectors.toList());
    }
}
