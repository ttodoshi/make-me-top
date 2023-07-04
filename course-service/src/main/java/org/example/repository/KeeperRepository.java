package org.example.repository;

import org.example.model.Keeper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeeperRepository extends JpaRepository<Keeper, Integer> {
}
