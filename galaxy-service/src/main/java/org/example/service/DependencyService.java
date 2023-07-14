package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.dependency.DependencyCreateRequest;
import org.example.dto.dependency.DependencyRequest;
import org.example.exception.classes.dependencyEX.DependencyAlreadyExistsException;
import org.example.exception.classes.dependencyEX.DependencyCouldNotBeCreatedException;
import org.example.exception.classes.dependencyEX.DependencyNotFoundException;
import org.example.exception.classes.systemEX.SystemNotFoundException;
import org.example.model.SystemDependency;
import org.example.repository.DependencyRepository;
import org.example.repository.StarSystemRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class DependencyService {
    private final DependencyRepository dependencyRepository;
    private final StarSystemRepository starSystemRepository;

    private final Logger logger = Logger.getLogger(DependencyService.class.getName());

    public List<SystemDependency> addDependency(List<DependencyCreateRequest> systemDependency) {
        List<SystemDependency> dependencies = new LinkedList<>();
        for (DependencyCreateRequest dependency : systemDependency) {
            if (dependency.getChildId().equals(dependency.getParentId()))
                throw new DependencyCouldNotBeCreatedException();
            if (!starSystemRepository.existsById(dependency.getChildId()) ||
                    (dependency.getParentId() != null && !starSystemRepository.existsById(dependency.getParentId())))
                throw new SystemNotFoundException();
            if (dependency.getParentId() == null && dependencyRepository.getSystemDependencyByChildIdAndParentNull(dependency.getChildId()) != null)
                throw new DependencyAlreadyExistsException();
            if (dependencyRepository.getSystemDependencyByChildIDAndParentId(dependency.getChildId(), dependency.getParentId()) != null)
                throw new DependencyAlreadyExistsException();
            dependencies.add(
                    dependencyRepository.save(
                            new SystemDependency(
                                    null,
                                    starSystemRepository.getReferenceById(dependency.getChildId()),
                                    dependency.getParentId() == null ? null : starSystemRepository.getReferenceById(dependency.getParentId()),
                                    dependency.getIsAlternative()
                            )
                    )
            );
        }
        return dependencies;
    }

    public Map<String, String> deleteDependency(DependencyRequest dependency) {
        Integer dependencyId;
        try {
            if (dependency.getParentId() == null)
                dependencyId = dependencyRepository.getSystemDependencyByChildIdAndParentNull(dependency.getChildId()).getDependencyId();
            else
                dependencyId = dependencyRepository.getSystemDependencyByChildIDAndParentId(dependency.getChildId(), dependency.getParentId()).getDependencyId();
            dependencyRepository.deleteById(dependencyId);
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new DependencyNotFoundException();
        }
        Map<String, String> response = new HashMap<>();
        response.put("message", "Зависимость " + dependencyId + " удалена");
        return response;
    }
}
