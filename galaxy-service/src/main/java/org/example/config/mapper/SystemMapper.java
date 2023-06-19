package org.example.config.mapper;

import org.example.dto.starsystem.StarSystemWithDependencies;
import org.example.model.StarSystem;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SystemMapper {

    public StarSystemWithDependencies systemToSystemWithDependencyModel(StarSystem system) {
        StarSystemWithDependencies systemDTOWithDependencies = new StarSystemWithDependencies();
        systemDTOWithDependencies.setSystemName(system.getSystemName());
        systemDTOWithDependencies.setSystemLevel(system.getSystemLevel());
        systemDTOWithDependencies.setSystemPosition(system.getSystemPosition());
        systemDTOWithDependencies.setOrbitId(system.getOrbitId());
        return systemDTOWithDependencies;
    }

}
