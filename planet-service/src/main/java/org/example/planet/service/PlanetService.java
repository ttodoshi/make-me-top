package org.example.planet.service;

import org.example.planet.dto.message.MessageDto;
import org.example.planet.dto.planet.CreatePlanetDto;
import org.example.planet.dto.planet.PlanetDto;
import org.example.planet.dto.planet.UpdatePlanetDto;

import java.util.List;
import java.util.Map;

public interface PlanetService {
    PlanetDto findPlanetById(Long planetId);

    List<PlanetDto> findPlanetsBySystemId(String authorizationHeader, Long systemId);

    Map<Long, PlanetDto> findPlanetsByPlanetIdIn(List<Long> planetIds);

    Map<Long, List<PlanetDto>> findPlanetsBySystemIdIn(List<Long> systemIds);

    List<Long> createPlanets(String authorizationHeader, Long systemId, List<CreatePlanetDto> planets);

    PlanetDto updatePlanet(String authorizationHeader, Long planetId, UpdatePlanetDto planet);

    MessageDto deletePlanetById(Long planetId);
}
