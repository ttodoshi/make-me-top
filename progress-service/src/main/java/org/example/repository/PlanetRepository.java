package org.example.repository;

import org.example.dto.planet.PlanetDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PlanetRepository {
    Optional<PlanetDto> findById(Integer planetId);

    List<PlanetDto> findPlanetsBySystemId(Integer systemId);

    Map<Integer, List<PlanetDto>> findPlanetsBySystemIdIn(List<Integer> systemIds);
}
