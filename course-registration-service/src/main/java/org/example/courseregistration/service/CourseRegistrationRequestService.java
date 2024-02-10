package org.example.courseregistration.service;

import org.example.courseregistration.dto.courserequest.CourseRegistrationRequestDto;

import java.util.List;
import java.util.Map;

public interface CourseRegistrationRequestService {
    CourseRegistrationRequestDto findProcessingCourseRegistrationRequestByPersonId(Long personId);

    Map<Long, CourseRegistrationRequestDto> findCourseRegistrationRequestsByRequestIdIn(String authorizationHeader, Long personId, List<Long> requestIds);
}
