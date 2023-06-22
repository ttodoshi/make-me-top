package org.example.repository;

import org.example.model.StarSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface StarSystemRepository extends JpaRepository<StarSystem, Integer> {
    @Query(value = "SELECT * FROM star_system  WHERE orbit_id = ?1", nativeQuery = true)
    List<StarSystem> getStarSystemsByOrbitId(Integer id);

    @Query(value = "SELECT system_id, system_position, system_level, system_name, s.orbit_id  FROM star_system s\n" +
            "LEFT JOIN orbit o ON s.orbit_id = o.orbit_id\n" +
            "LEFT JOIN galaxy gal ON gal.galaxy_id = o.galaxy_id\n" +
            "WHERE gal.galaxy_id =?1", nativeQuery = true)
    List<StarSystem> getStarSystemsByGalaxyId(Integer id);

    @Query(value = "SELECT * FROM star_system WHERE system_id =?1", nativeQuery = true)
    StarSystem checkExistsSystem(Integer id);

}
