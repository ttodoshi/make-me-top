package org.example.galaxy.repository;

import org.example.galaxy.model.Orbit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrbitRepository extends JpaRepository<Orbit, Long> {
    List<Orbit> findOrbitsByGalaxyId(Long galaxyId);
}
