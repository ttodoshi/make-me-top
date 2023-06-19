package org.example.repository;

import org.example.model.Planet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlanetRepository extends JpaRepository<Planet, Integer> {

    @Query(value = "SELECT * FROM planet WHERE system_id = ?1", nativeQuery = true)
    List<Planet> getListPlanetBySystemId(Integer id);

    @Query(value = "SELECT planet_id, planet_name, planet_number, planet.system_id FROM planet\n" +
            "JOIN star_system ON star_system.system_id = planet.system_id\n" +
            "JOIN orbit ON orbit.orbit_id = star_system.orbit_id\n" +
            "JOIN galaxy ON galaxy.galaxy_id = orbit.galaxy_id\n" +
            "WHERE galaxy.galaxy_id = ?1", nativeQuery = true)
    List<Planet> getAllPlanetsByGalaxyId(Integer galaxyId);
}
