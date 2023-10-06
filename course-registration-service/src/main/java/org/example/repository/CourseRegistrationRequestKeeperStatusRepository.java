package org.example.repository;

import org.example.model.CourseRegistrationRequestKeeperStatus;
import org.example.model.CourseRegistrationRequestKeeperStatusType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRegistrationRequestKeeperStatusRepository extends JpaRepository<CourseRegistrationRequestKeeperStatus, Integer> {
    Optional<CourseRegistrationRequestKeeperStatus> findCourseRegistrationRequestKeeperStatusByStatus(CourseRegistrationRequestKeeperStatusType status);
}
