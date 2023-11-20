package org.example.course.repository;

import org.example.course.dto.starsystem.StarSystemDto;

import java.util.List;

public interface StarSystemRepository {
    List<StarSystemDto> findStarSystemsByGalaxyId(Integer galaxyId);
}
