package org.example.repository.custom;

import org.example.dto.starsystem.StarSystemDTO;
import org.example.dto.starsystem.StarSystemWithDependenciesGetResponse;

import java.util.List;

public interface StarSystemRepository {
    List<StarSystemDTO> getSystemsByGalaxyId(Integer galaxyId);

    StarSystemWithDependenciesGetResponse getStarSystemWithDependencies(Integer systemId);
}
