package org.example.repository;

import org.example.model.Orbit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrbitRepository extends JpaRepository<Orbit, Integer> {

    @Query(value = "SELECT * FROM orbit WHERE galaxy_id = ?1", nativeQuery = true)
    List<Orbit> getOrbitsByGalaxyId(Integer id);


}
