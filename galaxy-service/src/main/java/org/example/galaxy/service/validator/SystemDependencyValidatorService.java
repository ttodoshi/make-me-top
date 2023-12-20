package org.example.galaxy.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.galaxy.dto.dependency.CreateDependencyDto;
import org.example.galaxy.exception.classes.dependency.DependencyAlreadyExistsException;
import org.example.galaxy.exception.classes.dependency.DependencyCouldNotBeCreatedException;
import org.example.galaxy.exception.classes.system.SystemNotFoundException;
import org.example.galaxy.exception.classes.system.SystemsFromDifferentGalaxiesException;
import org.example.galaxy.model.StarSystem;
import org.example.galaxy.repository.StarSystemRepository;
import org.example.galaxy.repository.SystemDependencyRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SystemDependencyValidatorService {
    private final SystemDependencyRepository systemDependencyRepository;
    private final StarSystemRepository starSystemRepository;

    @Transactional(readOnly = true)
    public void validateDependency(CreateDependencyDto dependency) {
        StarSystem child = starSystemRepository.findById(dependency.getChildId())
                .orElseThrow(() -> new SystemNotFoundException(dependency.getChildId()));
        StarSystem parent = starSystemRepository.findById(dependency.getParentId())
                .orElseThrow(() -> new SystemNotFoundException(dependency.getParentId()));
        if (!child.getOrbit().getGalaxyId().equals(parent.getOrbit().getGalaxyId()))
            throw new SystemsFromDifferentGalaxiesException(dependency.getChildId(), dependency.getParentId());
        if (dependency.getChildId().equals(dependency.getParentId()))
            throw new DependencyCouldNotBeCreatedException(dependency.getChildId(), dependency.getParentId());
        if (dependency.getParentId() == null) {
            if (systemDependencyRepository.getSystemDependencyByChildIdAndParentNull(dependency.getChildId()).isPresent())
                throw new DependencyAlreadyExistsException(dependency.getChildId(), dependency.getParentId());
        } else {
            if (systemDependencyRepository.getSystemDependencyByChildIdAndParentId(dependency.getChildId(), dependency.getParentId()).isPresent())
                throw new DependencyAlreadyExistsException(dependency.getChildId(), dependency.getParentId());
            if (systemDependencyRepository.getSystemDependencyByChildIdAndParentId(dependency.getParentId(), dependency.getChildId()).isPresent())
                throw new DependencyCouldNotBeCreatedException(dependency.getChildId(), dependency.getParentId());
        }
    }
}
