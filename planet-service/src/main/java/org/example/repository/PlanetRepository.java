package org.example.repository;

import org.example.model.Planet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanetRepository extends JpaRepository<Planet, Integer> {
    List<Planet> findPlanetsBySystemIdOrderByPlanetNumber(Integer systemId);

    void deletePlanetsBySystemId(Integer systemId);
}
