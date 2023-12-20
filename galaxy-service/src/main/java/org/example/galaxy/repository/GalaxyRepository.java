package org.example.galaxy.repository;

import org.example.galaxy.model.Galaxy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GalaxyRepository extends JpaRepository<Galaxy, Long> {
}
