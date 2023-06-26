package org.example.repository;

import org.example.model.SystemProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SystemProgressRepository extends JpaRepository<SystemProgress, Integer> {
    @Query(value = "SELECT * FROM system_progress WHERE person_id = ?1 AND system_id = ?2",
            nativeQuery = true)
    SystemProgress getSystemProgressForPerson(Integer personId, Integer systemId);

    @Query(value = "SELECT system_progress.system_id FROM system_progress\n" +
            "JOIN star_system ON star_system.system_id = system_progress.system_id\n" +
            "JOIN orbit ON orbit.orbit_id = star_system.orbit_id\n" +
            "WHERE system_progress.person_id = ?1 AND system_progress.progress = 0 AND orbit.galaxy_id = ?2", nativeQuery = true)
    List<Integer> findOpenedSystemsForPerson(Integer personId, Integer galaxyId);

    @Query(value = "SELECT system_progress.person_id, system_progress.system_id, system_progress.progress\n" +
            "FROM system_progress JOIN star_system ON star_system.system_id = system_progress.system_id\n" +
            "JOIN orbit ON orbit.orbit_id = star_system.orbit_id\n" +
            "WHERE person_id = ?1 AND system_progress.progress > 0 AND orbit.galaxy_id = ?2", nativeQuery = true)
    List<SystemProgress> findStudiedSystemsForPerson(Integer personId, Integer galaxyId);

    @Query(value = "SELECT star_system.system_id FROM star_system\n" +
            "JOIN orbit ON orbit.orbit_id = star_system.orbit_id\n" +
            "WHERE orbit.galaxy_id = ?2\n" +
            "AND star_system.system_id NOT IN (\n" +
            "SELECT system_progress.system_id FROM system_progress WHERE person_id = ?1\n" +
            ")", nativeQuery = true)
    List<Integer> findClosedSystemsForPerson(Integer personId, Integer galaxyId);
}
