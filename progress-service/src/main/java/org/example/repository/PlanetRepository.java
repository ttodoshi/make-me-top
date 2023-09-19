package org.example.repository;

import org.example.dto.planet.PlanetDto;

import java.util.List;
import java.util.Map;

public interface PlanetRepository {
    List<PlanetDto> findPlanetsBySystemId(Integer systemId);

    Map<Integer, List<PlanetDto>> findPlanetsBySystemIdIn(List<Integer> systemIds);
}
