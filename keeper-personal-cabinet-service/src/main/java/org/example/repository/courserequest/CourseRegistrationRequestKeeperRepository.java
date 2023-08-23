package org.example.repository.courserequest;

import org.example.model.courserequest.CourseRegistrationRequestKeeper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRegistrationRequestKeeperRepository extends JpaRepository<CourseRegistrationRequestKeeper, Integer> {
    List<CourseRegistrationRequestKeeper> findCourseRegistrationRequestKeepersByRequestId(Integer requestId);

    List<CourseRegistrationRequestKeeper> findCourseRegistrationRequestKeepersByKeeperIdAndStatusIdOrderByResponseDate(Integer keeperId, Integer statusId);

    Optional<CourseRegistrationRequestKeeper> findCourseRegistrationRequestKeeperByRequestIdAndKeeperId(Integer requestId, Integer keeperId);
}
