package org.example.repository;

import org.example.model.CourseRegistrationRequestStatus;
import org.example.model.CourseRegistrationRequestStatusType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRegistrationRequestStatusRepository extends JpaRepository<CourseRegistrationRequestStatus, Integer> {
    Optional<CourseRegistrationRequestStatus> findCourseRegistrationRequestStatusByStatus(CourseRegistrationRequestStatusType statusType);
}
