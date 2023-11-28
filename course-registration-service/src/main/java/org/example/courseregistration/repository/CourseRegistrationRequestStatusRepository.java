package org.example.courseregistration.repository;

import org.example.courseregistration.model.CourseRegistrationRequestStatus;
import org.example.courseregistration.model.CourseRegistrationRequestStatusType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRegistrationRequestStatusRepository extends JpaRepository<CourseRegistrationRequestStatus, Integer> {
    Optional<CourseRegistrationRequestStatus> findCourseRegistrationRequestStatusByStatus(CourseRegistrationRequestStatusType status);
}
