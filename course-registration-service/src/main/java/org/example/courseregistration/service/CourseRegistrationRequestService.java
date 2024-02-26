package org.example.courseregistration.service;

import org.example.courseregistration.dto.courserequest.CourseRegistrationRequestDto;
import org.example.courseregistration.model.CourseRegistrationRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface CourseRegistrationRequestService {
    @Transactional(readOnly = true)
    CourseRegistrationRequest findCourseRegistrationRequestById(Long requestId);

    CourseRegistrationRequestDto findProcessingCourseRegistrationRequestByPersonId(Long personId);

    Map<Long, CourseRegistrationRequestDto> findCourseRegistrationRequestsByRequestIdIn(String authorizationHeader, Long personId, List<Long> requestIds);
}
