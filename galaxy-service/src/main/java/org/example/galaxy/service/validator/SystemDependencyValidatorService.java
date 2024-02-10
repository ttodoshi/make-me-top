package org.example.galaxy.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.galaxy.dto.dependency.CreateDependencyDto;
import org.example.galaxy.exception.dependency.DependencyAlreadyExistsException;
import org.example.galaxy.exception.dependency.DependencyCouldNotBeCreatedException;
import org.example.galaxy.exception.system.SystemNotFoundException;
import org.example.galaxy.model.StarSystem;
import org.example.galaxy.repository.StarSystemRepository;
import org.example.galaxy.repository.SystemDependencyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SystemDependencyValidatorService {
    private final SystemDependencyRepository systemDependencyRepository;
    private final StarSystemRepository starSystemRepository;

    @Transactional(readOnly = true)
    public void validateDependency(CreateDependencyDto dependency) {
        if (dependencyCouldNotBeCreated(dependency)) {
            log.warn("dependency between child system {} and parent system {} could not be created", dependency.getChildId(), dependency.getParentId());
            throw new DependencyCouldNotBeCreatedException(dependency.getChildId(), dependency.getParentId());
        }
        if (dependencyExists(dependency)) {
            log.warn("dependency between system {} and system {} already exists", dependency.getChildId(), dependency.getParentId());
            throw new DependencyAlreadyExistsException(dependency.getChildId(), dependency.getParentId());
        }
    }

    private boolean dependencyCouldNotBeCreated(CreateDependencyDto dependency) {
        StarSystem child = starSystemRepository.findById(dependency.getChildId())
                .orElseThrow(() -> new SystemNotFoundException(dependency.getChildId()));
        StarSystem parent = starSystemRepository.findById(dependency.getParentId())
                .orElseThrow(() -> new SystemNotFoundException(dependency.getParentId()));
        return !child.getOrbit().getGalaxyId().equals(parent.getOrbit().getGalaxyId()) ||
                dependency.getChildId().equals(dependency.getParentId());
    }

    private boolean dependencyExists(CreateDependencyDto dependency) {
        if (dependency.getParentId() == null) {
            return systemDependencyRepository.existsSystemDependencyByChildIdAndParentNull(
                    dependency.getChildId()
            );
        } else {
            return systemDependencyRepository.existsSystemDependencyByChildIdAndParentId(
                    dependency.getChildId(), dependency.getParentId()
            ) || systemDependencyRepository.existsSystemDependencyByChildIdAndParentId(
                    dependency.getParentId(), dependency.getChildId()
            );
        }
    }
}
