package org.example.repository;

import org.example.dto.starsystem.StarSystemDto;

import java.util.List;

public interface StarSystemRepository {
    List<StarSystemDto> getSystemsByGalaxyId(Integer galaxyId);
}
