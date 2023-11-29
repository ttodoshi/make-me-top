package org.example.courseregistration.repository;

import org.example.courseregistration.model.CourseRegistrationRequestKeeperStatus;
import org.example.courseregistration.model.CourseRegistrationRequestKeeperStatusType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRegistrationRequestKeeperStatusRepository extends JpaRepository<CourseRegistrationRequestKeeperStatus, Long> {
    Optional<CourseRegistrationRequestKeeperStatus> findCourseRegistrationRequestKeeperStatusByStatus(CourseRegistrationRequestKeeperStatusType status);
}
