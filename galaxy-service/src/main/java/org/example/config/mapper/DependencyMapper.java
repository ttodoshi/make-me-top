package org.example.config.mapper;

import org.example.dto.dependency.DependencyGetInfoModel;
import org.example.model.SystemDependency;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DependencyMapper {

    DependencyGetInfoModel dependencyGetInfoModel;

    public DependencyGetInfoModel dependencyToDependencyParentModel(SystemDependency systemDependency) {
        dependencyGetInfoModel = new DependencyGetInfoModel();
        dependencyGetInfoModel.setSystemId(systemDependency.getChildId().getSystemId());
        dependencyGetInfoModel.setIsAlternative(systemDependency.getIsAlternative());
        dependencyGetInfoModel.setType("child");
        return dependencyGetInfoModel;
    }

    public DependencyGetInfoModel dependencyToDependencyChildModel(SystemDependency systemDependency) {
        dependencyGetInfoModel = new DependencyGetInfoModel();
        dependencyGetInfoModel.setSystemId(systemDependency.getParentId().getSystemId());
        dependencyGetInfoModel.setIsAlternative(systemDependency.getIsAlternative());
        dependencyGetInfoModel.setType("parent");
        return dependencyGetInfoModel;
    }
}
