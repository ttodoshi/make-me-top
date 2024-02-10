package org.example.progress.service;

import org.example.progress.dto.planet.PlanetDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PlanetService {
    PlanetDto findById(String authorizationHeader, Long planetId);

    List<PlanetDto> findPlanetsBySystemId(String authorizationHeader, Long systemId);

    Map<Long, List<PlanetDto>> findPlanetsBySystemIdIn(String authorizationHeader, List<Long> systemIds);
}
