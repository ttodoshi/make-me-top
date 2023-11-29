package org.example.planet.repository;

import org.example.planet.model.Planet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanetRepository extends JpaRepository<Planet, Long> {
    List<Planet> findPlanetsBySystemIdOrderByPlanetNumber(Long systemId);

    List<Planet> findPlanetsBySystemIdIn(List<Long> systemIds);

    List<Planet> findPlanetsByPlanetIdIn(List<Long> planetIds);

    void deletePlanetsBySystemId(Long systemId);
}
