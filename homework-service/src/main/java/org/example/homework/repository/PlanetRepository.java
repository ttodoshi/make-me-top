package org.example.homework.repository;

import org.example.homework.dto.planet.PlanetDto;

import java.util.Optional;

public interface PlanetRepository {
    Optional<PlanetDto> findById(Integer planetId);

    Boolean existsById(Integer themeId);
}
