package org.example.courseregistration.service;

import org.example.courseregistration.dto.galaxy.GalaxyDto;

public interface GalaxyService {
    GalaxyDto findGalaxyBySystemId(String authorizationHeader, Long systemId);
}
