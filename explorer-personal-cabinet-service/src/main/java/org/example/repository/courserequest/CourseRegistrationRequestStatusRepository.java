package org.example.repository.courserequest;

import org.example.model.courserequest.CourseRegistrationRequestStatus;
import org.example.model.courserequest.CourseRegistrationRequestStatusType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRegistrationRequestStatusRepository extends JpaRepository<CourseRegistrationRequestStatus, Integer> {
    Optional<CourseRegistrationRequestStatus> findCourseRegistrationRequestStatusByStatus(CourseRegistrationRequestStatusType statusType);
}
