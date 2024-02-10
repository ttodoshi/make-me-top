package org.example.person.service.api.planet;

import org.example.person.dto.planet.PlanetDto;

import java.util.List;
import java.util.Map;

public interface PlanetService {
    Map<Long, PlanetDto> findPlanetsByPlanetIdIn(String authorizationHeader, List<Long> planetIds);
}
