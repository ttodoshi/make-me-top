package org.example.repository;

import org.example.dto.galaxy.GalaxyDto;

public interface GalaxyRepository {
    GalaxyDto getGalaxyBySystemId(Integer systemId);
}
