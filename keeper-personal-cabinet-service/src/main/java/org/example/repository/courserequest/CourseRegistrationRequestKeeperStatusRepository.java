package org.example.repository.courserequest;

import org.example.model.courserequest.CourseRegistrationRequestKeeperStatus;
import org.example.model.courserequest.CourseRegistrationRequestKeeperStatusType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRegistrationRequestKeeperStatusRepository extends JpaRepository<CourseRegistrationRequestKeeperStatus, Integer> {
    Optional<CourseRegistrationRequestKeeperStatus> findCourseRegistrationRequestKeeperStatusByStatus(CourseRegistrationRequestKeeperStatusType status);
}
