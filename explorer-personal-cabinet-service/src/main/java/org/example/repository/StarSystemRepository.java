package org.example.repository;

import org.example.model.StarSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface StarSystemRepository extends JpaRepository<StarSystem, Integer> {
    @Query(value = "SELECT star_system.system_id, star_system.system_name, star_system.system_level, star_system.system_position, star_system.orbit_id\n" +
            "FROM star_system JOIN orbit ON orbit.orbit_id = star_system.orbit_id\n" +
            "WHERE orbit.galaxy_id = ?1 AND orbit.orbit_level = ?2", nativeQuery = true)
    List<StarSystem> getStarSystemsByGalaxyIdAndOrbitLevel(Integer galaxyId, Integer orbitLevel);
}
