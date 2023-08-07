package org.example.repository;

import org.example.model.courserequest.CourseRegistrationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CourseRegistrationRequestRepository extends JpaRepository<CourseRegistrationRequest, Integer> {

    @Query("SELECT crr FROM CourseRegistrationRequest crr\n" +
            "JOIN CourseRegistrationRequestStatus crrs ON crrs.statusId = crr.statusId\n" +
            "WHERE crrs.status = 'PROCESSING' AND crr.personId = :personId")
    Optional<CourseRegistrationRequest> findProcessingRequest(@Param("personId") Integer personId);

    @Query(value = "SELECT crr FROM CourseRegistrationRequest crr\n" +
            "JOIN CourseRegistrationRequestStatus crrs ON crrs.statusId = crr.statusId\n" +
            "WHERE crr.personId = :personId AND crr.courseId = :courseId AND crrs.status = 'APPROVED'")
    CourseRegistrationRequest findApprovedCourseRegistrationRequestByPersonIdAndCourseId(@Param("personId") Integer personId, @Param("courseId") Integer courseId);
}
