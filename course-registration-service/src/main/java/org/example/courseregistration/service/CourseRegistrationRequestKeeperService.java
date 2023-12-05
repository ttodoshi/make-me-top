package org.example.courseregistration.service;

import org.example.courseregistration.dto.courserequest.CourseRegistrationRequestKeeperDto;
import org.example.courseregistration.model.CourseRegistrationRequest;
import org.example.courseregistration.model.CourseRegistrationRequestKeeper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CourseRegistrationRequestKeeperService {
    CourseRegistrationRequestKeeper findCourseRegistrationRequestKeeperForPerson(Long personId, CourseRegistrationRequest request);

    void closeRequestForKeepers(CourseRegistrationRequest request);

    List<CourseRegistrationRequestKeeperDto> findCourseRegistrationRequestKeepersByRequestId(Long requestId);

    List<CourseRegistrationRequestKeeperDto> findProcessingCourseRegistrationRequestKeepersByKeeperIdIn(List<Long> keeperIds);
}
