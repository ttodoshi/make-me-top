package org.example.config.mapper;

import org.example.model.dependencyModel.DependencyGetInfoModel;
import org.example.model.modelDAO.SystemDependency;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DependencyMapper {

    DependencyGetInfoModel dependencyGetInfoModel;

    public DependencyGetInfoModel DependencyModelToDependencyParentModel(SystemDependency systemDependency) {
        dependencyGetInfoModel = new DependencyGetInfoModel();
        dependencyGetInfoModel.setSystemId(systemDependency.getChild().getSystemId());
        dependencyGetInfoModel.setIsAlternative(systemDependency.getIsAlternative());
        dependencyGetInfoModel.setType("child");
        return dependencyGetInfoModel;
    }
    public DependencyGetInfoModel DependencyModelToDependencyChildModel(SystemDependency systemDependency) {
        dependencyGetInfoModel = new DependencyGetInfoModel();
        dependencyGetInfoModel.setSystemId(systemDependency.getParent().getSystemId());
        dependencyGetInfoModel.setIsAlternative(systemDependency.getIsAlternative());
        dependencyGetInfoModel.setType("parent");
        return dependencyGetInfoModel;
    }
}
