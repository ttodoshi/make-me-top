package org.example.repository;

import org.example.model.Galaxy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GalaxyRepository extends JpaRepository<Galaxy, Integer> {
    @Query(value = "SELECT orbit.galaxy_id FROM star_system\n" +
            "JOIN orbit ON orbit.orbit_id = star_system.orbit_id\n" +
            "WHERE star_system.system_id = ?1", nativeQuery = true)
    Integer getGalaxyIdBySystemId(Integer systemId);
}
