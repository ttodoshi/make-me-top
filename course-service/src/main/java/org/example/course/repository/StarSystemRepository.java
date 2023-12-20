package org.example.course.repository;

import org.example.course.dto.system.StarSystemDto;

import java.util.List;

public interface StarSystemRepository {
    List<StarSystemDto> findStarSystemsByGalaxyId(Long galaxyId);
}
