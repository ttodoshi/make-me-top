package org.example.config.mapper;

import org.example.model.galaxyModel.GalaxyWithOrbitModel;
import org.example.model.modelDAO.Galaxy;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GalaxyMapper {
    public GalaxyWithOrbitModel mapGalaxy(Galaxy galaxy) {
        GalaxyWithOrbitModel galaxyWithOrbitModel = new GalaxyWithOrbitModel();
        galaxyWithOrbitModel.setGalaxyId(galaxy.getGalaxyId());
        galaxyWithOrbitModel.setGalaxyName(galaxy.getGalaxyName());
        return galaxyWithOrbitModel;
    }
}
