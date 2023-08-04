package org.example.repository;

import org.example.model.courserequest.CourseRegistrationRequest;
import org.example.model.courserequest.CourseRegistrationRequestStatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CourseRegistrationRequestRepository extends JpaRepository<CourseRegistrationRequest, Integer> {

    @Query("SELECT crr FROM CourseRegistrationRequest crr\n" +
            "JOIN CourseRegistrationRequestStatus crrs ON crrs.statusId = crr.statusId\n" +
            "WHERE crrs.status = 'PROCESSING'")
    Optional<CourseRegistrationRequest> findProcessingRequest();
}
