package org.example.repository;

import org.example.dto.starsystem.StarSystemDTO;

import java.util.List;

public interface StarSystemRepository {
    List<StarSystemDTO> getSystemsByGalaxyId(Integer galaxyId);
}
