package org.example.person.service.api.courseregistration;

import org.example.person.dto.courserequest.CourseRegistrationRequestKeeperDto;

import java.util.List;

public interface CourseRegistrationRequestKeeperService {
    List<CourseRegistrationRequestKeeperDto> findCourseRegistrationRequestKeepersByRequestId(String authorizationHeader, Long requestId);

    List<CourseRegistrationRequestKeeperDto> findProcessingCourseRegistrationRequestKeepersByKeeperIdIn(String authorizationHeader, List<Long> keeperIds);
}
