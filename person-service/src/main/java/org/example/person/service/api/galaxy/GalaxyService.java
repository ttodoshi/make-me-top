package org.example.person.service.api.galaxy;

import org.example.person.dto.galaxy.GalaxyDto;

import java.util.List;
import java.util.Map;

public interface GalaxyService {
    GalaxyDto findGalaxyBySystemId(String authorizationHeader, Long systemId);

    Map<Long, GalaxyDto> findGalaxiesBySystemIdIn(String authorizationHeader, List<Long> systemIds);
}
