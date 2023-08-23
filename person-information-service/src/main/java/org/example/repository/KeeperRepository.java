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
            "\tp.personId, p.firstName, p.lastName, p.patronymic, k.keeperId\n" +
            ") FROM CourseRegistrationRequest crr\n" +
            "JOIN CourseRegistrationRequestStatus crrs ON crrs.statusId = crr.statusId\n" +
            "JOIN CourseRegistrationRequestKeeper crrk ON crrk.requestId = crr.requestId\n" +
            "JOIN Keeper k ON k.keeperId = crrk.keeperId\n" +
            "JOIN Person p ON p.personId = k.personId\n" +
            "WHERE crr.personId = :personId AND crrs.status = 'ACCEPTED' AND crr.courseId = :courseId")
    KeeperDTO getKeeperForPersonOnCourse(@Param("personId") Integer personId,
                                         @Param("courseId") Integer courseId);
}
