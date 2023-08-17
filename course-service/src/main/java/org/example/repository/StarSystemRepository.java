package org.example.repository;

import org.example.dto.starsystem.StarSystemDTO;

public interface StarSystemRepository {
    StarSystemDTO[] getSystemsByGalaxyId(Integer galaxyId);
}
