package org.example.config.mapper;

import org.example.dto.starsystem.SystemDependencyModelDto;
import org.example.model.SystemDependency;
import org.springframework.stereotype.Component;

@Component
public class DependencyMapper {
    public SystemDependencyModelDto dependencyToDependencyChildModel(SystemDependency systemDependency) {
        return new SystemDependencyModelDto(
                systemDependency.getChild().getSystemId(),
                systemDependency.getChild().getSystemName(),
                "child",
                systemDependency.getIsAlternative()
        );
    }

    public SystemDependencyModelDto dependencyToDependencyParentModel(SystemDependency systemDependency) {
        return new SystemDependencyModelDto(
                systemDependency.getParent().getSystemId(),
                systemDependency.getParent().getSystemName(),
                "parent",
                systemDependency.getIsAlternative()
        );
    }
}
