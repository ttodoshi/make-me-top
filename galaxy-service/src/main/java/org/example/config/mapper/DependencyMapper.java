package org.example.config.mapper;

import org.example.dto.starsystem.SystemDependencyModelDto;
import org.example.model.SystemDependency;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DependencyMapper {
    public SystemDependencyModelDto dependencyToDependencyChildModel(SystemDependency systemDependency) {
        return new SystemDependencyModelDto(
                systemDependency.getChild().getSystemId(),
                "child",
                systemDependency.getIsAlternative()
        );
    }

    public SystemDependencyModelDto dependencyToDependencyParentModel(SystemDependency systemDependency) {
        return new SystemDependencyModelDto(
                systemDependency.getParent().getSystemId(),
                "parent",
                systemDependency.getIsAlternative()
        );
    }
}
