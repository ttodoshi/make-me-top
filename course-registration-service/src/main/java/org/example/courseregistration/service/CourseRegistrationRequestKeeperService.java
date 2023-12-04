package org.example.courseregistration.service;

import org.example.courseregistration.model.CourseRegistrationRequest;
import org.example.courseregistration.model.CourseRegistrationRequestKeeper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CourseRegistrationRequestKeeperService {
    CourseRegistrationRequestKeeper findCourseRegistrationRequestKeeperForPerson(Long personId, CourseRegistrationRequest request);

    void closeRequestForKeepers(CourseRegistrationRequest request);

    List<CourseRegistrationRequestKeeper> findCourseRegistrationRequestKeepersByRequestId(Long requestId);

    List<CourseRegistrationRequestKeeper> findProcessingCourseRegistrationRequestKeepersByKeeperIdIn(List<Long> keeperIds);
}
