package org.example.progress.repository;

import org.example.progress.dto.planet.PlanetDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PlanetRepository {
    Optional<PlanetDto> findById(Long planetId);

    List<PlanetDto> findPlanetsBySystemId(Long systemId);

    Map<Long, List<PlanetDto>> findPlanetsBySystemIdIn(List<Long> systemIds);
}
