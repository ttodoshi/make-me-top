package org.example.progress.service;

import org.example.progress.dto.galaxy.GetGalaxyDto;

public interface GalaxyService {
    GetGalaxyDto findGalaxyById(Long galaxyId);
}
