package org.example.service;

import org.example.model.CourseRegistrationRequest;
import org.example.model.CourseRegistrationRequestKeeper;
import org.example.model.CourseRegistrationRequestKeeperStatusType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CourseRegistrationRequestKeeperService {
    CourseRegistrationRequestKeeper saveKeeperResponseWithStatus(CourseRegistrationRequestKeeper keeperResponse,
                                                                 CourseRegistrationRequestKeeperStatusType status);

    CourseRegistrationRequestKeeper findCourseRegistrationRequestForAuthenticatedKeeper(CourseRegistrationRequest request);

    void closeRequestToOtherKeepersOnCourse(CourseRegistrationRequest request);

    List<CourseRegistrationRequestKeeper> findCourseRegistrationRequestKeepersByRequestId(Integer requestId);

    List<CourseRegistrationRequestKeeper> findProcessingCourseRegistrationRequestKeepersByKeeperIdIn(List<Integer> keeperIds);
}
