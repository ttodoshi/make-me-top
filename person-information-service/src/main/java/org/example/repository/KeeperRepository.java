package org.example.repository;

import org.example.dto.keeper.KeeperDTO;
import org.example.model.Keeper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface KeeperRepository extends JpaRepository<Keeper, Integer> {
    @Query(value = "SELECT COUNT(*) FROM course.keeper\n" +
            "WHERE person_id = ?1", nativeQuery = true)
    Integer getKeeperSystemsCount(Integer authenticatedPersonId);

    @Query(value = "SELECT new org.example.dto.keeper.KeeperDTO(\n" +
            "   p.personId, p.firstName, p.lastName, p.patronymic, k.keeperId\n" +
            ")" +
            "FROM Explorer e\n" +
            "JOIN ExplorerGroup eg ON eg.groupId = e.groupId\n" +
            "JOIN Keeper k ON k.keeperId = eg.keeperId\n" +
            "JOIN Person p ON p.personId = k.personId\n" +
            "WHERE e.explorerId = :explorerId")
    KeeperDTO getKeeperForExplorer(@Param("explorerId") Integer explorerId);
}
