package org.example.repository;

import org.example.model.Galaxy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GalaxyRepository extends JpaRepository<Galaxy, Integer> {
    @Query(value = "SELECT * FROM galaxy", nativeQuery = true)
    List<Galaxy> getAllGalaxy();
}
