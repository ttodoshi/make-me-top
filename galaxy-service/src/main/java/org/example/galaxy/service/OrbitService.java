package org.example.galaxy.service;

import org.example.galaxy.dto.message.MessageDto;
import org.example.galaxy.dto.orbit.CreateOrbitWithStarSystemsDto;
import org.example.galaxy.dto.orbit.GetOrbitWithStarSystemsDto;
import org.example.galaxy.dto.orbit.OrbitDto;
import org.example.galaxy.dto.orbit.UpdateOrbitDto;

public interface OrbitService {
    GetOrbitWithStarSystemsDto findOrbitWithSystemList(Long orbitId);

    OrbitDto findOrbitById(Long orbitId);

    Long createOrbit(Long galaxyId, CreateOrbitWithStarSystemsDto orbitRequest);

    OrbitDto updateOrbit(Long orbitId, UpdateOrbitDto orbit);

    MessageDto deleteOrbit(Long orbitId);
}
