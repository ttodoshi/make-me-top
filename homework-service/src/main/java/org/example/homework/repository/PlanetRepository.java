package org.example.homework.repository;

import org.example.homework.dto.planet.PlanetDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PlanetRepository {
    Optional<PlanetDto> findById(Long planetId);

    Map<Long, PlanetDto> findPlanetsByPlanetIdIn(List<Long> planetIds);
}
