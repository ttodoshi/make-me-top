package org.example.galaxy.service;

import lombok.RequiredArgsConstructor;
import org.example.galaxy.dto.dependency.CreateDependencyDto;
import org.example.galaxy.dto.dependency.DependencyDto;
import org.example.galaxy.dto.message.MessageDto;
import org.example.galaxy.exception.classes.dependency.DependencyNotFoundException;
import org.example.galaxy.model.SystemDependency;
import org.example.galaxy.repository.SystemDependencyRepository;
import org.example.galaxy.service.validator.SystemDependencyValidatorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemDependencyService {
    private final SystemDependencyRepository systemDependencyRepository;

    private final SystemDependencyValidatorService systemDependencyValidatorService;

    @Transactional
    public List<Long> addDependency(List<CreateDependencyDto> systemDependencies) {
        return systemDependencies.stream()
                .peek(systemDependencyValidatorService::validateDependency)
                .map(d -> systemDependencyRepository.save(
                                new SystemDependency(
                                        d.getChildId(),
                                        d.getParentId(),
                                        d.getIsAlternative()
                                )
                        ).getDependencyId()
                ).collect(Collectors.toList());
    }

    @Transactional
    public MessageDto deleteDependency(DependencyDto dependency) {
        Long dependencyId = findDependency(dependency).getDependencyId();
        systemDependencyRepository.deleteById(dependencyId);
        return new MessageDto("Зависимость " + dependencyId + " удалена");
    }

    private SystemDependency findDependency(DependencyDto dependencyDto) {
        Optional<SystemDependency> dependency;
        if (dependencyDto.getParentId() == null)
            dependency = systemDependencyRepository.getSystemDependencyByChildIdAndParentNull(dependencyDto.getChildId());
        else
            dependency = systemDependencyRepository.getSystemDependencyByChildIdAndParentId(dependencyDto.getChildId(), dependencyDto.getParentId());
        return dependency.orElseThrow(DependencyNotFoundException::new);
    }
}
