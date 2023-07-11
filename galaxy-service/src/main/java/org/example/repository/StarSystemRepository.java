package org.example.repository;

import org.example.model.StarSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface StarSystemRepository extends JpaRepository<StarSystem, Integer> {
    List<StarSystem> findStarSystemsByOrbitId(Integer orbitId);

    @Query(value = "SELECT system_id, system_position, system_level, system_name, s.orbit_id  FROM galaxy.star_system s\n" +
            "LEFT JOIN galaxy.orbit o ON s.orbit_id = o.orbit_id\n" +
            "LEFT JOIN galaxy.galaxy g ON g.galaxy_id = o.galaxy_id\n" +
            "WHERE g.galaxy_id =?1", nativeQuery = true)
    List<StarSystem> getStarSystemsByGalaxyId(Integer galaxyId);
}
