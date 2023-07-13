package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.starsystem.StarSystemsForUser;
import org.example.dto.starsystem.SystemWithProgress;
import org.example.dto.systemprogress.CourseThemeProgressDTO;
import org.example.dto.systemprogress.PlanetWithProgress;
import org.example.dto.systemprogress.ProgressUpdateRequest;
import org.example.dto.systemprogress.SystemWithPlanetsProgress;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.exception.classes.progressEX.PlanetAlreadyCompletedException;
import org.example.exception.classes.progressEX.SystemParentsNotCompletedException;
import org.example.exception.classes.progressEX.UnexpectedCourseThemeException;
import org.example.exception.classes.progressEX.UnexpectedProgressValueException;
import org.example.model.Explorer;
import org.example.model.Person;
import org.example.model.course.Course;
import org.example.model.course.CourseTheme;
import org.example.model.galaxy.StarSystem;
import org.example.model.galaxy.SystemDependency;
import org.example.model.progress.CourseThemeProgress;
import org.example.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.*;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class SystemProgressService {
    private final PlanetProgressRepository planetProgressRepository;
    private final DependencyRepository dependencyRepository;
    private final ExplorerRepository explorerRepository;
    private final CourseRepository courseRepository;
    private final CourseThemeRepository courseThemeRepository;

    private final RestTemplate restTemplate;
    private final ModelMapper mapper;
    private final Logger logger = Logger.getLogger(SystemProgressService.class.getName());
    @Value("${app_galaxy_url}")
    private String GALAXY_APP_URL;

    public StarSystemsForUser getSystemsProgressForCurrentUser(Integer galaxyId, String token) {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Set<Integer> openedSystems = new HashSet<>();
        Set<SystemWithProgress> studiedSystems = new HashSet<>();
        Set<Integer> closedSystems = new HashSet<>();
        for (StarSystem system : getSystemsByGalaxyId(galaxyId, token)) {
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

    private StarSystem[] getSystemsByGalaxyId(Integer galaxyId, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);
            return restTemplate.exchange(GALAXY_APP_URL + "/galaxy/" + galaxyId + "/system/", HttpMethod.GET, requestEntity, StarSystem[].class).getBody();
        } catch (ResourceAccessException e) {
            throw new ConnectException();
        }
    }

    public SystemWithPlanetsProgress getPlanetsProgressBySystemId(Integer systemId) {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Course course = courseRepository.findById(systemId).orElseThrow(CourseNotFoundException::new);
        Explorer explorer = explorerRepository.findExplorerByPersonIdAndCourseId(authenticatedPerson.getPersonId(), systemId).orElseThrow(ExplorerNotFoundException::new);
        SystemWithPlanetsProgress systemWithPlanetsProgress = new SystemWithPlanetsProgress();
        systemWithPlanetsProgress.setCourseId(course.getCourseId());
        systemWithPlanetsProgress.setTitle(course.getTitle());
        List<PlanetWithProgress> planetWithProgresses = new LinkedList<>();
        for (CourseTheme ct : courseThemeRepository.findCourseThemesByCourseIdOrderByCourseThemeNumberAsc(systemId)) {
            Optional<CourseThemeProgress> planetProgress = planetProgressRepository.findCourseThemeProgressByExplorerIdAndCourseThemeId(explorer.getExplorerId(), ct.getCourseThemeId());
            if (planetProgress.isPresent())
                planetWithProgresses.add(
                        new PlanetWithProgress(ct.getCourseThemeId(), ct.getTitle(), planetProgress.get().getProgress())
                );
            else {
                planetWithProgresses.add(
                        new PlanetWithProgress(ct.getCourseThemeId(), ct.getTitle(), 0)
                );
            }
        }
        systemWithPlanetsProgress.setPlanetsWithProgress(planetWithProgresses);
        return systemWithPlanetsProgress;
    }

    @Transactional
    public Map<String, Object> updatePlanetProgress(Integer planetId, ProgressUpdateRequest updateRequest) {
        final Integer personId = getAuthenticatedPersonId();
        Explorer explorer = findExplorer(personId, planetId);
        saveProgress(explorer, planetId, updateRequest);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Прогресс планеты " + planetId +
                " обновлён на " + updateRequest.getProgress());
        return response;
    }

    private Integer getAuthenticatedPersonId() {
        final Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }

    private Explorer findExplorer(Integer personId, Integer planetId) {
        Integer courseId = courseRepository.getCourseIdByThemeId(planetId)
                .orElseThrow(CourseThemeNotFoundException::new);
        return explorerRepository.findExplorerByPersonIdAndCourseId(personId, courseId)
                .orElseThrow(ExplorerNotFoundException::new);
    }

    private void saveProgress(Explorer explorer, Integer themeId, ProgressUpdateRequest updateRequest) {
        if (updateRequest.getProgress() > 100 || updateRequest.getProgress() < 0)
            throw new UnexpectedProgressValueException();
        if (hasUncompletedParents(explorer.getPersonId(), explorer.getCourseId()))
            throw new SystemParentsNotCompletedException();
        Integer currentThemeId = getCurrentCourseThemeId(explorer.getCourseId());
        if (!currentThemeId.equals(themeId))
            throw new UnexpectedCourseThemeException(themeId, currentThemeId);
        Optional<CourseThemeProgress> courseThemeProgressOptional = planetProgressRepository
                .findCourseThemeProgressByExplorerIdAndCourseThemeId(explorer.getExplorerId(), themeId);
        CourseThemeProgress courseThemeProgress;
        if (courseThemeProgressOptional.isEmpty()) {
            planetProgressRepository.save(
                    mapper.map(
                            new CourseThemeProgressDTO(
                                    explorer.getExplorerId(), themeId, updateRequest.getProgress()),
                            CourseThemeProgress.class));
            return;
        } else {
            courseThemeProgress = courseThemeProgressOptional.get();
        }
        if (courseThemeProgress.getProgress().equals(100))
            throw new PlanetAlreadyCompletedException();
        else if (courseThemeProgress.getProgress() > updateRequest.getProgress())
            throw new UnexpectedProgressValueException();
        else {
            courseThemeProgress.setProgress(updateRequest.getProgress());
            planetProgressRepository.save(courseThemeProgress);
        }
    }

    public Integer getCurrentCourseThemeId(Integer systemId) {
        List<PlanetWithProgress> planetsProgress = getPlanetsProgressBySystemId(systemId).getPlanetsWithProgress();
        for (PlanetWithProgress planet : planetsProgress) {
            if (!planet.getProgress().equals(100))
                return planet.getCourseThemeId();
        }
        return planetsProgress.get(planetsProgress.size() - 1).getCourseThemeId();
    }

    public boolean hasUncompletedParents(Integer personId, Integer systemId) {
        boolean parentsUncompleted = false;
        for (SystemDependency parent : dependencyRepository
                .getListSystemDependencyChild(systemId)) {
            if (parent.getParentId() == null)
                return false;
            Optional<Explorer> explorer = explorerRepository.findExplorerByPersonIdAndCourseId(personId, parent.getParentId().getSystemId());
            if (explorer.isEmpty() || planetProgressRepository.getSystemProgress(
                    explorer.get().getExplorerId(), explorer.get().getCourseId()) < 100) {
                parentsUncompleted = true;
            } else if (parent.getIsAlternative())
                return false;
        }
        return parentsUncompleted;
    }
}
