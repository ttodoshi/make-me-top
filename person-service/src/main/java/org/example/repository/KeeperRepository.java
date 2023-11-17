package org.example.repository;

import org.example.model.Keeper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface KeeperRepository extends JpaRepository<Keeper, Integer> {
    Optional<Keeper> findKeeperByPersonIdAndCourseId(Integer personId, Integer courseId);

    List<Keeper> findKeepersByPersonId(Integer personId);

    List<Keeper> findKeepersByCourseId(Integer courseId);

    List<Keeper> findKeepersByKeeperIdIn(List<Integer> keeperIds);

    List<Keeper> findKeepersByPersonIdAndCourseIdIn(Integer personId, List<Integer> courseIds);

    void deleteKeepersByCourseId(Integer courseId);
}
