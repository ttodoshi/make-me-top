package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.dependency.DependencyCreateRequest;
import org.example.dto.dependency.DependencyRequest;
import org.example.exception.classes.dependencyEX.DependencyNotFoundException;
import org.example.exception.classes.systemEX.SystemNotFoundException;
import org.example.model.SystemDependency;
import org.example.repository.DependencyRepository;
import org.example.repository.StarSystemRepository;
import org.example.service.validator.SystemDependencyValidatorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SystemDependencyService {
    private final DependencyRepository dependencyRepository;
    private final StarSystemRepository starSystemRepository;

    private final SystemDependencyValidatorService systemDependencyValidatorService;

    @Transactional
    public List<SystemDependency> addDependency(List<DependencyCreateRequest> systemDependency) {
        List<SystemDependency> dependencies = new ArrayList<>();
        for (DependencyCreateRequest dependency : systemDependency) {
            systemDependencyValidatorService.validateDependency(dependency);
            dependencies.add(
                    dependencyRepository.save(
                            new SystemDependency(
                                    starSystemRepository.findById(dependency.getChildId()).orElseThrow(() -> new SystemNotFoundException(dependency.getChildId())),
                                    dependency.getParentId() == null ? null : starSystemRepository.findById(dependency.getParentId()).orElseThrow(() -> new SystemNotFoundException(dependency.getParentId())),
                                    dependency.getIsAlternative()
                            )
                    )
            );
        }
        return dependencies;
    }

    public Map<String, String> deleteDependency(DependencyRequest dependency) {
        Integer dependencyId = getDependencyId(dependency);
        dependencyRepository.deleteById(dependencyId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Зависимость " + dependencyId + " удалена");
        return response;
    }

    private Integer getDependencyId(DependencyRequest dependencyRequest) {
        SystemDependency dependency;
        if (dependencyRequest.getParentId() == null)
            dependency = dependencyRepository.getSystemDependencyByChildIdAndParentNull(dependencyRequest.getChildId());
        else
            dependency = dependencyRepository.getSystemDependencyByChildIDAndParentId(dependencyRequest.getChildId(), dependencyRequest.getParentId());
        if (dependency == null)
            throw new DependencyNotFoundException();
        return dependency.getDependencyId();
    }
}
