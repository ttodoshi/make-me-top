package org.example.galaxy.repository;

import org.example.galaxy.model.StarSystem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StarSystemRepository extends JpaRepository<StarSystem, Integer> {
    List<StarSystem> findStarSystemsByOrbitId(Integer orbitId);

    List<StarSystem> findStarSystemsByOrbit_GalaxyId(Integer galaxyId);
}
