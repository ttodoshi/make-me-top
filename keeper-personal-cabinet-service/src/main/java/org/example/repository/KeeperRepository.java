package org.example.repository;

import org.example.model.Keeper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KeeperRepository extends JpaRepository<Keeper, Integer> {
    Optional<Keeper> findKeeperByPersonIdAndCourseId(Integer personId, Integer courseId);
}
