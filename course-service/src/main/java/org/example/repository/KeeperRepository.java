package org.example.repository;

import org.example.dto.keeper.KeeperDto;
import org.example.model.Keeper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KeeperRepository extends JpaRepository<Keeper, Integer> {
    @Query(value = "SELECT new org.example.dto.keeper.KeeperDto(p.personId, p.firstName, p.lastName, p.patronymic, k.keeperId) FROM Keeper k " +
            "JOIN Person p ON p.personId = k.personId " +
            "WHERE k.courseId = :courseId")
    List<KeeperDto> findKeepersByCourseId(@Param("courseId") Integer courseId);

    Optional<Keeper> findKeeperByPersonIdAndCourseId(Integer personId, Integer courseId);

    @Query(value = "SELECT k FROM Explorer e\n" +
            "JOIN ExplorerGroup eg ON eg.groupId = e.groupId\n" +
            "JOIN Keeper k ON k.keeperId = eg.keeperId\n" +
            "WHERE e.explorerId = :explorerId")
    Keeper getKeeperForExplorer(@Param("explorerId") Integer explorerId);
}
