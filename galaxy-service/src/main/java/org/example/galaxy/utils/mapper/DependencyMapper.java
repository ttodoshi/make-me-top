package org.example.galaxy.utils.mapper;

import org.example.galaxy.dto.system.SystemDependencyModelDto;
import org.example.galaxy.model.SystemDependency;

public class DependencyMapper {
    public static SystemDependencyModelDto dependencyToDependencyChildModel(SystemDependency systemDependency) {
        return new SystemDependencyModelDto(
                systemDependency.getChild().getSystemId(),
                systemDependency.getChild().getSystemName(),
                "child",
                systemDependency.getIsAlternative()
        );
    }

    public static SystemDependencyModelDto dependencyToDependencyParentModel(SystemDependency systemDependency) {
        return new SystemDependencyModelDto(
                systemDependency.getParent().getSystemId(),
                systemDependency.getParent().getSystemName(),
                "parent",
                systemDependency.getIsAlternative()
        );
    }
}
