package org.example.courseregistration.service;

import org.example.courseregistration.dto.courserequest.CourseRegistrationRequestKeeperDto;
import org.example.courseregistration.model.CourseRegistrationRequest;
import org.example.courseregistration.model.CourseRegistrationRequestKeeper;

import java.util.List;

public interface CourseRegistrationRequestKeeperService {
    CourseRegistrationRequestKeeper findCourseRegistrationRequestKeeperForPerson(String authorizationHeader, Long personId, CourseRegistrationRequest request);

    void closeRequestForKeepers(CourseRegistrationRequest request);

    List<CourseRegistrationRequestKeeperDto> findCourseRegistrationRequestKeepersByRequestId(Long authenticatedPersonId, Long requestId);

    List<CourseRegistrationRequestKeeperDto> findProcessingCourseRegistrationRequestKeepersByKeeperIdIn(String authorizationHeader, Long authenticatedPersonId, List<Long> keeperIds);
}
