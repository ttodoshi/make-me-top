package org.example.person.repository;

import org.example.person.dto.galaxy.GalaxyDto;

import java.util.List;
import java.util.Map;

public interface GalaxyRepository {
    GalaxyDto findGalaxyBySystemId(Long systemId);

    Map<Long, GalaxyDto> findGalaxiesBySystemIdIn(List<Long> systemIds);
}
