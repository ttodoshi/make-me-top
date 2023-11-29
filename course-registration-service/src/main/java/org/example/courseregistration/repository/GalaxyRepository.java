package org.example.courseregistration.repository;

import org.example.courseregistration.dto.galaxy.GalaxyDto;

public interface GalaxyRepository {
    GalaxyDto findGalaxyBySystemId(Long systemId);
}
