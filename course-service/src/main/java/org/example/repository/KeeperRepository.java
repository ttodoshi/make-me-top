package org.example.repository;

import org.example.dto.keeper.KeeperDTO;
import org.example.model.Keeper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KeeperRepository extends JpaRepository<Keeper, Integer> {
    @Query(value = "SELECT new org.example.dto.keeper.KeeperDTO(p.personId, p.firstName, p.lastName, p.patronymic, k.keeperId) FROM Keeper k " +
            "JOIN Person p ON p.personId = k.personId " +
            "WHERE k.courseId = :courseId")
    List<KeeperDTO> findKeepersByCourseId(@Param("courseId") Integer courseId);

    Optional<Keeper> findKeeperByPersonIdAndCourseId(Integer personId, Integer courseId);

    @Query(value = "SELECT k FROM CourseRegistrationRequest crr\n" +
            "JOIN CourseRegistrationRequestStatus crrs ON crrs.statusId = crr.statusId\n" +
            "JOIN CourseRegistrationRequestKeeper crrk ON crrk.requestId = crr.requestId\n" +
            "JOIN Keeper k ON k.keeperId = crrk.keeperId\n" +
            "JOIN Person p ON p.personId = k.personId\n" +
            "WHERE crr.personId = :personId AND crrs.status = 'ACCEPTED' AND crr.courseId = :courseId")
    Keeper getKeeperForPersonOnCourse(@Param("personId") Integer personId,
                                      @Param("courseId") Integer courseId);
}
