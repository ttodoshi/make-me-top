package org.example.galaxy.repository;

import org.example.galaxy.model.StarSystem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StarSystemRepository extends JpaRepository<StarSystem, Long> {
    List<StarSystem> findStarSystemsBySystemIdIn(List<Long> systemIds);

    List<StarSystem> findStarSystemsByOrbitId(Long orbitId);

    List<StarSystem> findStarSystemsByOrbit_GalaxyId(Long galaxyId);

    boolean existsStarSystemByOrbit_GalaxyIdAndSystemName(Long galaxyId, String systemName);

    boolean existsStarSystemByOrbit_GalaxyIdAndSystemIdNotAndSystemName(Long galaxyId, Long systemId, String systemName);
}
