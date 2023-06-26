package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.systemprogress.ProgressUpdateRequest;
import org.example.exception.classes.progressEX.ProgressDecreaseException;
import org.example.exception.classes.progressEX.SystemParentsNotCompletedException;
import org.example.model.Person;
import org.example.model.SystemDependency;
import org.example.model.SystemProgress;
import org.example.repository.DependencyRepository;
import org.example.repository.SystemProgressRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class SystemProgressService {
    private final SystemProgressRepository systemProgressRepository;
    private final DependencyRepository dependencyRepository;

    private final Logger logger = Logger.getLogger(GalaxyService.class.getName());

    @Transactional
    public Map<String, Object> updateSystemProgress(Integer systemId, ProgressUpdateRequest updateRequest) {
        final Integer personId = getAuthenticatedPersonId();
        saveProgress(personId, systemId, updateRequest);
        Map<String, Object> response = new HashMap<>();
        if (updateRequest.getProgress() >= 100) {
            List<Integer> newOpenedSystems = openSystems(
                    personId, getPreviouslyBlockedSystems(personId, systemId));
            if (!newOpenedSystems.isEmpty())
                response.put("Открыты системы", newOpenedSystems);
        }
        response.put("message", "Прогресс системы" + systemId +
                " обновлён на " + updateRequest.getProgress());
        return response;
    }

    private Integer getAuthenticatedPersonId() {
        final Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }

    private void saveProgress(Integer personId, Integer systemId, ProgressUpdateRequest updateRequest) {
        SystemProgress updatedSystemProgress = systemProgressRepository
                .getSystemProgressForPerson(personId, systemId);
        if (updatedSystemProgress == null || hasUncompletedParents(personId, systemId))
            throw new SystemParentsNotCompletedException();
        if (updatedSystemProgress.getProgress() > updateRequest.getProgress())
            throw new ProgressDecreaseException();
        updatedSystemProgress.setProgress(updateRequest.getProgress());
        systemProgressRepository.save(updatedSystemProgress);
    }

    private List<Integer> getPreviouslyBlockedSystems(Integer personId, Integer systemId) {
        List<Integer> openedSystems = new LinkedList<>();
        for (SystemDependency child : dependencyRepository.getListSystemDependencyParent(systemId)) {
            if (!hasUncompletedParents(personId, child.getChildId().getSystemId())) {
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
            SystemProgress parentProgress = systemProgressRepository
                    .getSystemProgressForPerson(
                            personId, parent.getParentId().getSystemId());
            if (parentProgress == null || parentProgress.getProgress() < 100) {
                parentsUncompleted = true;
            }
        }
        return parentsUncompleted;
    }

    private List<Integer> openSystems(Integer personId, List<Integer> systems) {
        for (Integer systemId : systems) {
            systemProgressRepository.save(new SystemProgress(personId, systemId, 0));
        }
        return systems;
    }
}
