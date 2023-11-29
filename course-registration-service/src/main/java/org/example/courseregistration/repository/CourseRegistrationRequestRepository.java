package org.example.courseregistration.repository;

import org.example.courseregistration.model.CourseRegistrationRequest;
import org.example.courseregistration.model.CourseRegistrationRequestKeeper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRegistrationRequestRepository extends JpaRepository<CourseRegistrationRequest, Long> {
    @Query("SELECT crr FROM CourseRegistrationRequest crr\n" +
            " WHERE crr.personId = :personId AND crr.status.status != 'ACCEPTED'")
    Optional<CourseRegistrationRequest> findCourseRegistrationRequestByPersonIdAndStatus_NotAccepted(@Param("personId") Long personId);

    List<CourseRegistrationRequest> findCourseRegistrationRequestsByRequestIdIn(List<Long> requestIds);

    @Query(value = "SELECT crr FROM CourseRegistrationRequest crr\n" +
            "JOIN CourseRegistrationRequestKeeper crrk ON crrk.requestId = crr.requestId\n" +
            "WHERE crrk.keeperId = :keeperId AND crr.status.status = 'APPROVED' AND crrk.status.status = 'APPROVED'\n" +
            "ORDER BY crrk.responseDate")
    List<CourseRegistrationRequest> findApprovedRequestsByKeeperId(@Param("keeperId") Long keeperId);

    @Query(value = "SELECT crrk FROM CourseRegistrationRequest crr\n" +
            "JOIN CourseRegistrationRequestKeeper crrk ON crrk.requestId = crr.requestId\n" +
            "WHERE crrk.keeperId IN :keeperIds AND crr.status.status = 'APPROVED' AND crrk.status.status = 'APPROVED'\n" +
            "ORDER BY crrk.responseDate")
    List<CourseRegistrationRequestKeeper> findApprovedKeeperRequestsByKeeperIdIn(@Param("keeperIds") List<Long> keeperIds);

    void deleteCourseRegistrationRequestsByCourseId(Long courseId);
}
