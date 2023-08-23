package org.example.repository.courserequest;

import org.example.model.courserequest.CourseRegistrationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRegistrationRequestRepository extends JpaRepository<CourseRegistrationRequest, Integer> {
    @Query(value = "SELECT crr FROM CourseRegistrationRequest crr\n" +
            "JOIN CourseRegistrationRequestStatus crrs ON crrs.statusId = crr.statusId\n" +
            "JOIN CourseRegistrationRequestKeeper crrk ON crrk.requestId = crr.requestId\n" +
            "JOIN CourseRegistrationRequestKeeperStatus crrks ON crrks.statusId = crrk.statusId\n" +
            "JOIN Keeper k ON k.keeperId = crrk.keeperId\n" +
            "WHERE k.personId = :personId AND crr.courseId = :courseId " +
            "AND crrs.status = 'APPROVED' AND crrks.status = 'APPROVED'" +
            "ORDER BY crrk.responseDate")
    List<CourseRegistrationRequest> findApprovedRequestsByKeeperPersonIdAndCourseId(@Param("personId") Integer personId,
                                                                                    @Param("courseId") Integer courseId);
}
