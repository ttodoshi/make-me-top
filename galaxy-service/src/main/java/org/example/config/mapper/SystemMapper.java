package org.example.config.mapper;

import org.example.model.modelDAO.StarSystem;
import org.example.model.systemModel.SystemCreateModel;
import org.example.model.systemModel.SystemWithDependencyModel;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SystemMapper {

    SystemWithDependencyModel systemWithDependencyModel;
    StarSystem starSystem;

    public SystemWithDependencyModel systemToSystemWithDependencyModel(StarSystem system) {
        systemWithDependencyModel = new SystemWithDependencyModel();
        systemWithDependencyModel.setSystemId(system.getSystemId());
        systemWithDependencyModel.setSystemName(system.getSystemName());
        systemWithDependencyModel.setSystemLevel(system.getSystemLevel());
        systemWithDependencyModel.setPositionSystem(system.getPositionSystem());
        systemWithDependencyModel.setOrbitId(system.getOrbitId());
        return systemWithDependencyModel;
    }

    public StarSystem systemCreateModelToStarSystem(SystemCreateModel model) {
        starSystem = new StarSystem();
        starSystem.setSystemId(model.getSystemId());
        starSystem.setPositionSystem(model.getPositionSystem());
        starSystem.setSystemName(model.getSystemName());
        starSystem.setOrbitId(model.getOrbitId());
        starSystem.setSystemLevel(model.getSystemLevel());
        return starSystem;
    }


}
