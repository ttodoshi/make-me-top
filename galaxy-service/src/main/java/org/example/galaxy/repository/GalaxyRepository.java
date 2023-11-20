package org.example.galaxy.repository;

import org.example.galaxy.model.Galaxy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GalaxyRepository extends JpaRepository<Galaxy, Integer> {
    @Query(value = "SELECT g FROM Galaxy g\n" +
            "JOIN Orbit o ON o.galaxyId = g.galaxyId\n" +
            "JOIN StarSystem s ON s.orbitId = o.orbitId\n" +
            "WHERE s.systemId = :systemId")
    Optional<Galaxy> findGalaxyBySystemId(@Param("systemId") Integer systemId);
}
