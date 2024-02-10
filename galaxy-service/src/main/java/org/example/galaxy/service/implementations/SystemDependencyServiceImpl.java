package org.example.galaxy.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.galaxy.dto.dependency.CreateDependencyDto;
import org.example.galaxy.dto.dependency.DependencyDto;
import org.example.galaxy.dto.message.MessageDto;
import org.example.galaxy.exception.dependency.DependencyNotFoundException;
import org.example.galaxy.model.SystemDependency;
import org.example.galaxy.repository.SystemDependencyRepository;
import org.example.galaxy.service.SystemDependencyService;
import org.example.galaxy.service.validator.SystemDependencyValidatorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SystemDependencyServiceImpl implements SystemDependencyService {
    private final SystemDependencyRepository systemDependencyRepository;

    private final SystemDependencyValidatorService systemDependencyValidatorService;

    @Override
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

    @Override
    @Transactional
    public MessageDto deleteDependency(DependencyDto dependency) {
        Long dependencyId = findDependency(dependency).getDependencyId();
        systemDependencyRepository.deleteById(dependencyId);
        return new MessageDto("Зависимость " + dependencyId + " удалена");
    }

    private SystemDependency findDependency(DependencyDto dependencyDto) {
        Optional<SystemDependency> dependency;
        if (dependencyDto.getParentId() == null)
            dependency = systemDependencyRepository.findSystemDependencyByChildIdAndParentNull(dependencyDto.getChildId());
        else
            dependency = systemDependencyRepository.findSystemDependencyByChildIdAndParentId(dependencyDto.getChildId(), dependencyDto.getParentId());
        return dependency.orElseThrow(() -> {
            log.warn("dependency between child system {} and parent system {} not found", dependencyDto.getChildId(), dependencyDto.getParentId());
            return new DependencyNotFoundException();
        });
    }
}
