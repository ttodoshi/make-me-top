package org.example.person.repository;

import org.example.person.dto.planet.PlanetDto;

import java.util.List;
import java.util.Map;

public interface PlanetRepository {
    Map<Long, PlanetDto> findPlanetsByPlanetIdIn(List<Long> planetIds);
}
