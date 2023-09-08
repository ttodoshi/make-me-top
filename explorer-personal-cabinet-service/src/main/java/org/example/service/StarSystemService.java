package org.example.service;

import org.example.dto.starsystem.StarSystemDto;
import org.example.dto.starsystem.GetStarSystemWithDependenciesDto;

import java.util.List;

public interface StarSystemService {
    List<StarSystemDto> getSystemsByGalaxyId(Integer galaxyId);

    GetStarSystemWithDependenciesDto getStarSystemWithDependencies(Integer systemId);
}
