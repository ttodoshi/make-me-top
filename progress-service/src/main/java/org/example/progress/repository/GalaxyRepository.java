package org.example.progress.repository;

import org.example.progress.dto.galaxy.GetGalaxyDto;

public interface GalaxyRepository {
    GetGalaxyDto findGalaxyById(Integer galaxyId);
}
