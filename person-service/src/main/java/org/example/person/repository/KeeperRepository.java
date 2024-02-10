package org.example.person.repository;

import org.example.person.model.Keeper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface KeeperRepository extends JpaRepository<Keeper, Long> {
    Optional<Keeper> findKeeperByPersonIdAndCourseId(Long personId, Long courseId);

    List<Keeper> findKeepersByCourseId(Long courseId);

    List<Keeper> findKeepersByPersonId(Long personId);

    List<Keeper> findKeepersByPersonIdIn(List<Long> personIds);

    List<Keeper> findKeepersByKeeperIdIn(List<Long> keeperIds);

    List<Keeper> findKeepersByCourseIdIn(List<Long> courseIds);

    List<Keeper> findKeepersByPersonIdAndCourseIdIn(Long personId, List<Long> courseIds);

    boolean existsKeeperByPersonIdAndCourseId(Long personId, Long courseId);

    void deleteKeepersByCourseId(Long courseId);
}
