package org.example.repository.custom;

import org.example.dto.starsystem.StarSystemDTO;
import org.example.dto.starsystem.StarSystemWithDependenciesGetResponse;

public interface StarSystemRepository {
    StarSystemDTO[] getSystemsByGalaxyId(Integer galaxyId);

    StarSystemWithDependenciesGetResponse getStarSystemWithDependencies(Integer systemId);
}
