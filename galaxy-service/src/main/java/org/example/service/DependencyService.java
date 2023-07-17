package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.dependency.CreateDependencyRequest;
import org.example.dto.dependency.DependencyRequest;
import org.example.exception.classes.dependencyEX.DependencyAlreadyExistsException;
import org.example.exception.classes.dependencyEX.DependencyCouldNotBeCreatedException;
import org.example.exception.classes.dependencyEX.DependencyNotFoundException;
import org.example.exception.classes.systemEX.SystemNotFoundException;
import org.example.model.SystemDependency;
import org.example.repository.DependencyRepository;
import org.example.repository.StarSystemRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DependencyService {
    private final DependencyRepository dependencyRepository;
    private final StarSystemRepository starSystemRepository;

    @Transactional
    public List<SystemDependency> addDependency(List<CreateDependencyRequest> systemDependency) {
        List<SystemDependency> dependencies = new LinkedList<>();
        for (CreateDependencyRequest dependency : systemDependency) {
            if (dependency.getChildId().equals(dependency.getParentId()))
                throw new DependencyCouldNotBeCreatedException();
            if ((dependency.getParentId() == null && dependencyRepository.getSystemDependencyByChildIdAndParentNull(dependency.getChildId()) != null) ||
                    (dependencyRepository.getSystemDependencyByChildIDAndParentId(dependency.getChildId(), dependency.getParentId()) != null))
                throw new DependencyAlreadyExistsException();
            dependencies.add(
                    dependencyRepository.save(
                            new SystemDependency(
                                    null,
                                    starSystemRepository.findById(dependency.getChildId()).orElseThrow(SystemNotFoundException::new),
                                    dependency.getParentId() == null ? null : starSystemRepository.findById(dependency.getParentId()).orElseThrow(SystemNotFoundException::new),
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
        } catch (NullPointerException e) {
            throw new DependencyNotFoundException();
        }
        dependencyRepository.deleteById(dependencyId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Зависимость " + dependencyId + " удалена");
        return response;
    }
}
