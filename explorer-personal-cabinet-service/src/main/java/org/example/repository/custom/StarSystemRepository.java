package org.example.repository.custom;

import org.example.dto.starsystem.StarSystemDto;
import org.example.dto.starsystem.GetStarSystemWithDependenciesDto;

import java.util.List;

public interface StarSystemRepository {
    List<StarSystemDto> getSystemsByGalaxyId(Integer galaxyId);

    GetStarSystemWithDependenciesDto getStarSystemWithDependencies(Integer systemId);
}
