package org.example.galaxy.repository;

import org.example.galaxy.model.Galaxy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GalaxyRepository extends JpaRepository<Galaxy, Long> {
    boolean existsGalaxyByGalaxyName(String galaxyName);

    boolean existsGalaxyByGalaxyIdNotAndGalaxyName(Long galaxyId, String galaxyName);
}
