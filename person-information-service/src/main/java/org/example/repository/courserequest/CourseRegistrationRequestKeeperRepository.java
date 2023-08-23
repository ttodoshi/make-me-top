package org.example.repository.courserequest;

import org.example.model.courserequest.CourseRegistrationRequestKeeper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRegistrationRequestKeeperRepository extends JpaRepository<CourseRegistrationRequestKeeper, Integer> {
    List<CourseRegistrationRequestKeeper> findAllByRequestId(Integer requestId);
}
