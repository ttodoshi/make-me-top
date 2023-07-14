package org.example.repository;

import org.example.model.Galaxy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GalaxyRepository extends JpaRepository<Galaxy, Integer> {
}
