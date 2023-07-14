package org.example.config.mapper;

import org.example.dto.starsystem.SystemDependencyModel;
import org.example.model.SystemDependency;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DependencyMapper {

    SystemDependencyModel systemDependencyModel;

    public SystemDependencyModel dependencyToDependencyParentModel(SystemDependency systemDependency) {
        systemDependencyModel = new SystemDependencyModel();
        systemDependencyModel.setSystemId(systemDependency.getChild().getSystemId());
        systemDependencyModel.setIsAlternative(systemDependency.getIsAlternative());
        systemDependencyModel.setType("child");
        return systemDependencyModel;
    }

    public SystemDependencyModel dependencyToDependencyChildModel(SystemDependency systemDependency) {
        systemDependencyModel = new SystemDependencyModel();
        systemDependencyModel.setSystemId(systemDependency.getParent().getSystemId());
        systemDependencyModel.setIsAlternative(systemDependency.getIsAlternative());
        systemDependencyModel.setType("parent");
        return systemDependencyModel;
    }
}
