package org.example.homework.service;

import org.example.homework.dto.planet.PlanetDto;

import java.util.List;
import java.util.Map;

public interface PlanetService {
    PlanetDto findById(String authorizationHeader, Long planetId);

    Map<Long, PlanetDto> findPlanetsByPlanetIdIn(String authorizationHeader, List<Long> planetIds);
}
