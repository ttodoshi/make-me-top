package org.example.repository;

import org.example.model.CourseRegistrationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRegistrationRequestRepository extends JpaRepository<CourseRegistrationRequest, Integer> {
    @Query("SELECT crr FROM CourseRegistrationRequest crr\n" +
            " WHERE crr.personId = :personId AND crr.status.status = 'PROCESSING'")
    Optional<CourseRegistrationRequest> findCourseRegistrationRequestByPersonIdAndStatus_ProcessingStatus(@Param("personId") Integer personId);

    List<CourseRegistrationRequest> findCourseRegistrationRequestsByRequestIdIn(List<Integer> requestIds);

    @Query(value = "SELECT crr FROM CourseRegistrationRequest crr\n" +
            "JOIN CourseRegistrationRequestKeeper crrk ON crrk.requestId = crr.requestId\n" +
            "WHERE crrk.keeperId = :keeperId AND crr.status.status = 'APPROVED' AND crrk.status.status = 'APPROVED'\n" +
            "ORDER BY crrk.responseDate")
    List<CourseRegistrationRequest> findApprovedRequestsByKeeperId(@Param("keeperId") Integer keeperId);
}