package org.example.repository;

import org.example.model.Keeper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface KeeperRepository extends JpaRepository<Keeper, Integer> {
    Optional<Keeper> findKeeperByPersonIdAndCourseId(Integer personId, Integer courseId);

    @Query(value = "SELECT k FROM CourseRegistrationRequest crr\n" +
            "JOIN CourseRegistrationRequestStatus crrs ON crrs.statusId = crr.statusId\n" +
            "JOIN Keeper k ON k.keeperId = crr.keeperId\n" +
            "JOIN Person p ON p.personId = k.personId\n" +
            "WHERE crr.personId = :personId AND crrs.status = 'APPROVED' AND crr.courseId = :courseId")
    Keeper getKeeperForPersonOnCourse(@Param("personId") Integer personId,
                                      @Param("courseId") Integer courseId);
}
