package org.example.service;

import org.example.model.courserequest.CourseRegistrationRequest;
import org.example.model.courserequest.CourseRegistrationRequestKeeper;
import org.example.model.courserequest.CourseRegistrationRequestKeeperStatusType;
import org.springframework.stereotype.Service;

@Service
public interface CourseRegistrationRequestKeeperService {
    CourseRegistrationRequestKeeper saveKeeperResponseWithStatus(CourseRegistrationRequestKeeper keeperResponse,
                                                                 CourseRegistrationRequestKeeperStatusType status);

    CourseRegistrationRequestKeeper findCourseRegistrationRequestForAuthenticatedKeeper(CourseRegistrationRequest request);

    void closeRequestToOtherKeepersOnCourse(CourseRegistrationRequest request);

    boolean isRequestPersonallyForKeeper(CourseRegistrationRequest request);

    void openRequestToOtherKeepersOnCourse(CourseRegistrationRequest request);
}
