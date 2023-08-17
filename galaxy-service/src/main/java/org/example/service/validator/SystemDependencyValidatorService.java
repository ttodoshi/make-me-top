package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.dependency.DependencyCreateRequest;
import org.example.exception.classes.dependencyEX.DependencyAlreadyExistsException;
import org.example.exception.classes.dependencyEX.DependencyCouldNotBeCreatedException;
import org.example.repository.DependencyRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SystemDependencyValidatorService {
    private final DependencyRepository dependencyRepository;

    public void validateDependency(DependencyCreateRequest dependency) {
        if (dependency.getChildId().equals(dependency.getParentId()))
            throw new DependencyCouldNotBeCreatedException(dependency.getChildId(), dependency.getParentId());
        if (dependency.getParentId() == null) {
            if (dependencyRepository.getSystemDependencyByChildIdAndParentNull(dependency.getChildId()) != null)
                throw new DependencyAlreadyExistsException(dependency.getChildId(), dependency.getParentId());
        } else {
            if (dependencyRepository.getSystemDependencyByChildIDAndParentId(dependency.getChildId(), dependency.getParentId()) != null)
                throw new DependencyAlreadyExistsException(dependency.getChildId(), dependency.getParentId());
            if (dependencyRepository.getSystemDependencyByChildIDAndParentId(dependency.getParentId(), dependency.getChildId()) != null)
                throw new DependencyCouldNotBeCreatedException(dependency.getChildId(), dependency.getParentId());
        }
    }
}
