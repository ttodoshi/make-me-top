package org.example.repository;

import org.example.model.modelDAO.StarSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SystemRepository extends JpaRepository<StarSystem, Integer> {
    @Query(value = "SELECT * FROM Star_System  WHERE orbit_id = ?1", nativeQuery = true)
    List<StarSystem> getStarSystemByOrbitId(Integer id);

    @Query(value = "SELECT system_id, position_system, system_level, system_name, s.orbit_id  FROM star_system s\n" +
            "LEFT JOIN orbit o ON s.orbit_id = o.orbit_id\n" +
            "LEFT JOIN galaxy gal ON gal.galaxy_id = o.galaxy_id\n" +
            "WHERE gal.galaxy_id =?1", nativeQuery = true)
    List<StarSystem> getStarSystemByGalaxyId(Integer id);

    @Query(value = "SELECT * FROM Star_System WHERE system_id =?1", nativeQuery = true)
    StarSystem checkExistsSystem(Integer id);

}
