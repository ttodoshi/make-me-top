package org.example.course.service;

import org.example.course.dto.system.StarSystemDto;

import java.util.List;

public interface StarSystemService {
    List<StarSystemDto> findStarSystemsByGalaxyId(String authorizationHeader, Long galaxyId);
}
