package org.example.person.repository;

import org.example.person.dto.planet.PlanetDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PlanetRepository {
    Optional<PlanetDto> findById(Integer planetId);

    Map<Integer, PlanetDto> findPlanetsByPlanetIdIn(List<Integer> planetIds);
}
