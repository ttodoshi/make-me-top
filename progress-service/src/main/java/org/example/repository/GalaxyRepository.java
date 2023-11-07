package org.example.repository;

import org.example.dto.galaxy.GetGalaxyDto;

public interface GalaxyRepository {
    GetGalaxyDto getGalaxyById(Integer galaxyId);
}
