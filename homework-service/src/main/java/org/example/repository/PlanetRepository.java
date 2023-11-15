package org.example.repository;

import org.example.dto.planet.PlanetDto;

import java.util.Optional;

public interface PlanetRepository {
    Optional<PlanetDto> findById(Integer planetId);

    Boolean existsById(Integer themeId);
}
