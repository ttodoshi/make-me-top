package org.example.repository;

import org.example.model.Planet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlanetRepository extends JpaRepository<Planet, Integer> {
    @Query(value = "SELECT * FROM galaxy.planet WHERE system_id = ?1", nativeQuery = true)
    List<Planet> getPlanetsBySystemId(Integer id);
}
