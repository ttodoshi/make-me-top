package org.example.galaxy.service;

import org.example.galaxy.dto.message.MessageDto;
import org.example.galaxy.dto.system.CreateStarSystemDto;
import org.example.galaxy.dto.system.GetStarSystemWithDependenciesDto;
import org.example.galaxy.dto.system.StarSystemDto;
import org.example.galaxy.dto.system.UpdateStarSystemDto;

import java.util.List;

public interface StarSystemService {
    GetStarSystemWithDependenciesDto findStarSystemByIdWithDependencies(Long systemId);

    StarSystemDto findStarSystemById(Long systemId);

    List<StarSystemDto> findStarSystemsByGalaxyId(Long galaxyId);

    Long createSystem(Long orbitId, CreateStarSystemDto systemRequest);

    StarSystemDto updateSystem(Long systemId, UpdateStarSystemDto starSystem);

    MessageDto deleteSystem(Long systemId);
}
