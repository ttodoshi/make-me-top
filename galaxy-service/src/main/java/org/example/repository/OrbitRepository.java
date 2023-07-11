package org.example.repository;

import org.example.model.Orbit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrbitRepository extends JpaRepository<Orbit, Integer> {
    List<Orbit> findOrbitsByGalaxyId(Integer galaxyId);
}
