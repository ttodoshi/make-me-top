package org.example.courseregistration.service;

import org.example.courseregistration.model.CourseRegistrationRequest;
import org.example.courseregistration.model.CourseRegistrationRequestKeeper;
import org.example.courseregistration.model.CourseRegistrationRequestKeeperStatusType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CourseRegistrationRequestKeeperService {
    CourseRegistrationRequestKeeper saveKeeperResponseWithStatus(CourseRegistrationRequestKeeper keeperResponse,
                                                                 CourseRegistrationRequestKeeperStatusType status);

    CourseRegistrationRequestKeeper findCourseRegistrationRequestForAuthenticatedKeeper(CourseRegistrationRequest request);

    void closeRequestToOtherKeepers(CourseRegistrationRequest request);

    List<CourseRegistrationRequestKeeper> findCourseRegistrationRequestKeepersByRequestId(Long requestId);

    List<CourseRegistrationRequestKeeper> findProcessingCourseRegistrationRequestKeepersByKeeperIdIn(List<Long> keeperIds);
}
