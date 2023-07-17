package org.example.config.mapper;

import org.example.dto.starsystem.SystemDependencyModel;
import org.example.model.SystemDependency;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DependencyMapper {
    public SystemDependencyModel dependencyToDependencyChildModel(SystemDependency systemDependency) {
        return new SystemDependencyModel(
                systemDependency.getChild().getSystemId(),
                "child",
                systemDependency.getIsAlternative()
        );
    }

    public SystemDependencyModel dependencyToDependencyParentModel(SystemDependency systemDependency) {
        return new SystemDependencyModel(
                systemDependency.getParent().getSystemId(),
                "parent",
                systemDependency.getIsAlternative()
        );
    }
}
