package org.example.repository;

import org.example.model.Keeper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface KeeperRepository extends JpaRepository<Keeper, Integer> {
    Optional<Keeper> findKeeperByPersonIdAndCourseId(Integer personId, Integer courseId);

    @Query(value = "SELECT COUNT(*) FROM course.keeper\n" +
            "WHERE person_id = ?1", nativeQuery = true)
    Integer getKeeperSystemsCount(Integer authenticatedPersonId);
}
