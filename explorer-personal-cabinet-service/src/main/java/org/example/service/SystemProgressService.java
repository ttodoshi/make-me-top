package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.starsystem.StarSystemsForUser;
import org.example.dto.starsystem.SystemWithProgress;
import org.example.dto.systemprogress.ProgressUpdateRequest;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.progressEX.ProgressDecreaseException;
import org.example.exception.classes.progressEX.SystemParentsNotCompletedException;
import org.example.exception.classes.progressEX.UpdateProgressException;
import org.example.model.*;
import org.example.repository.DependencyRepository;
import org.example.repository.ExplorerRepository;
import org.example.repository.SystemProgressRepository;
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
    private final SystemProgressRepository systemProgressRepository;
    private final DependencyRepository dependencyRepository;
    private final ExplorerRepository explorerRepository;

    private final RestTemplate restTemplate;

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
                        systemProgressRepository.getSystemProgress(
                                explorer.getExplorerId()).getProgress()));
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
    public Map<String, Object> updateCourseProgress(Integer systemId, ProgressUpdateRequest updateRequest) {
        final Integer personId = getAuthenticatedPersonId();
        Explorer explorer = explorerRepository.findExplorerByPersonIdAndCourseId(personId, systemId);
        saveProgress(explorer, updateRequest);
        try {
            Map<String, Object> response = new HashMap<>();
            if (updateRequest.getProgress() >= 100) {
                List<Integer> newOpenedSystems = getPreviouslyBlockedSystems(explorer);
                if (!newOpenedSystems.isEmpty())
                    response.put("Открыты системы", newOpenedSystems);
            }
            response.put("message", "Прогресс системы " + systemId +
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

    private void saveProgress(Explorer explorer, ProgressUpdateRequest updateRequest) {
        CourseProgress updatedCourseProgress = systemProgressRepository
                .getSystemProgress(explorer.getExplorerId());
        if (updatedCourseProgress == null || hasUncompletedParents(
                explorer.getPersonId(), explorer.getCourseId()))
            throw new SystemParentsNotCompletedException();
        if (updatedCourseProgress.getProgress() > updateRequest.getProgress())
            throw new ProgressDecreaseException();
        updatedCourseProgress.setProgress(updateRequest.getProgress());
        systemProgressRepository.save(updatedCourseProgress);
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
            if (explorer == null || systemProgressRepository.getSystemProgress(
                    explorer.getExplorerId()).getProgress() < 100) {
                parentsUncompleted = true;
            } else if (parent.getIsAlternative())
                return false;
        }
        return parentsUncompleted;
    }
}
