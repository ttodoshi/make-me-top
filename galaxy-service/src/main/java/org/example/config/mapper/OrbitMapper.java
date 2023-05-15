package org.example.config.mapper;

import org.example.model.modelDAO.Orbit;
import org.example.model.orbitModel.OrbitCreateModel;
import org.example.model.orbitModel.OrbitModel;
import org.example.model.orbitModel.OrbitWithSystemModel;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrbitMapper {
    OrbitWithSystemModel orbitWithSystemModel;
    Orbit orbit;
    OrbitModel orbitModel;

    public OrbitWithSystemModel orbitToOrbitWithSystemModel(Orbit orbit) {
        orbitWithSystemModel = new OrbitWithSystemModel();
        orbitWithSystemModel.setOrbitId(orbit.getOrbitId());
        orbitWithSystemModel.setLevelOrbit(orbit.getOrbitLevel());
        orbitWithSystemModel.setCountSystem(orbit.getCountSystem());
        orbitWithSystemModel.setGalaxyId(orbit.getGalaxyId());
        return orbitWithSystemModel;
    }

    public Orbit createOrbitModelToOrbit(OrbitCreateModel model) {
        orbit = new Orbit();
        orbit.setOrbitId(model.getOrbitId());
        orbit.setOrbitLevel(model.getLevelOrbit());
        orbit.setCountSystem(model.getCountSystem());
        orbit.setGalaxyId(model.getGalaxyId());
        return orbit;
    }

    public OrbitModel createOrbitModelToOrbitModel(Orbit model) {
        orbitModel = new OrbitModel();
        orbitModel.setOrbitId(model.getOrbitId());
        orbitModel.setLevelOrbit(model.getOrbitLevel());
        orbitModel.setCountSystem(model.getCountSystem());
        orbitModel.setGalaxyId(model.getGalaxyId());
        return orbitModel;
    }
}
