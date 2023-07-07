package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.starsystem.StarSystemsForUser;
import org.example.dto.starsystem.SystemWithProgress;
import org.example.dto.systemprogress.CourseThemeProgressDTO;
import org.example.dto.systemprogress.ProgressUpdateRequest;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.progressEX.ProgressDecreaseException;
import org.example.exception.classes.progressEX.SystemParentsNotCompletedException;
import org.example.exception.classes.progressEX.UpdateProgressException;
import org.example.model.*;
import org.example.repository.CourseRepository;
import org.example.repository.DependencyRepository;
import org.example.repository.ExplorerRepository;
import org.example.repository.PlanetProgressRepository;
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

    private final RestTemplate restTemplate;
    private final ModelMapper mapper;
    private final Logger logger = Logger.getLogger(SystemProgressService.class.getName());
    @Value("${app_galaxy_url}")
    private String GALAXY_APP_URL;
    @Value("${get_systems_by_galaxy_id}")
    private String GET_SYSTEMS_BY_GALAXY_ID_URL;

    public StarSystemsForUser getSystemsProgressForCurrentUser(Integer galaxyId, String token) {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Set<Integer> openedSystems = new HashSet<>();
        Set<SystemWithProgress> studiedSystems = new HashSet<>();
        Set<Integer> closedSystems = new HashSet<>();
        for (StarSystem system : getSystemsByGalaxyId(galaxyId, token)) {
            Explorer explorer = explorerRepository.findExplorerByPersonIdAndCourseId(
                    authenticatedPerson.getPersonId(), system.getSystemId());
            if (explorer != null) {
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
            return restTemplate.exchange(GALAXY_APP_URL + GET_SYSTEMS_BY_GALAXY_ID_URL + galaxyId + "/system/", HttpMethod.GET, requestEntity, StarSystem[].class).getBody();
        } catch (ResourceAccessException e) {
            throw new ConnectException();
        }
    }

    @Transactional
    public Map<String, Object> updateCourseThemeProgress(Integer planetId, ProgressUpdateRequest updateRequest) {
        final Integer personId = getAuthenticatedPersonId();
        Explorer explorer = explorerRepository.findExplorerByPersonIdAndCourseId(personId, courseRepository.getCourseIdByThemeId(planetId));
        saveProgress(explorer, planetId, updateRequest);
        try {
            Map<String, Object> response = new HashMap<>();
            if (updateRequest.getProgress() >= 100) {
                List<Integer> newOpenedSystems = getPreviouslyBlockedSystems(explorer);
                if (!newOpenedSystems.isEmpty())
                    response.put("Открыты системы", newOpenedSystems);
            }
            response.put("message", "Прогресс планеты " + planetId +
                    " обновлён на " + updateRequest.getProgress());
            return response;
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new UpdateProgressException();
        }
    }

    private Integer getAuthenticatedPersonId() {
        final Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }

    private void saveProgress(Explorer explorer, Integer themeId, ProgressUpdateRequest updateRequest) {
        if (explorer == null)
            throw new SystemParentsNotCompletedException();
        CourseThemeProgress updatedCourseProgress = planetProgressRepository
                .getPlanetProgressByExplorerIdAndPlanetId(explorer.getExplorerId(), themeId);
        if (hasUncompletedParents(explorer.getPersonId(), explorer.getCourseId()))
            throw new SystemParentsNotCompletedException();
        if (updatedCourseProgress == null)
            planetProgressRepository.save(
                    mapper.map(
                            new CourseThemeProgressDTO(
                                    explorer.getExplorerId(), themeId, updateRequest.getProgress()),
                            CourseThemeProgress.class));
        else if (updatedCourseProgress.getProgress() > updateRequest.getProgress())
            throw new ProgressDecreaseException();
        else {
            updatedCourseProgress.setProgress(updateRequest.getProgress());
            planetProgressRepository.save(updatedCourseProgress);
        }
    }

    private List<Integer> getPreviouslyBlockedSystems(Explorer explorer) {
        List<Integer> openedSystems = new LinkedList<>();
        for (SystemDependency child : dependencyRepository.getListSystemDependencyParent(explorer.getCourseId())) {
            if (!hasUncompletedParents(explorer.getPersonId(), child.getChildId().getSystemId())) {
                openedSystems.add(child.getChildId().getSystemId());
            }
        }
        return openedSystems;
    }

    private boolean hasUncompletedParents(Integer personId, Integer systemId) {
        boolean parentsUncompleted = false;
        for (SystemDependency parent : dependencyRepository
                .getListSystemDependencyChild(systemId)) {
            if (parent.getParentId() == null)
                return false;
            Explorer explorer = explorerRepository.findExplorerByPersonIdAndCourseId(personId, parent.getParentId().getSystemId());
            if (explorer == null || planetProgressRepository.getSystemProgress(
                    explorer.getExplorerId(), explorer.getCourseId()) < 100) {
                parentsUncompleted = true;
            } else if (parent.getIsAlternative())
                return false;
        }
        return parentsUncompleted;
    }
}
