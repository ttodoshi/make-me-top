package org.example.repository;

import org.example.dto.courserequest.CourseRegistrationRequestDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CourseRegistrationRequestRepository {
    Optional<CourseRegistrationRequestDto> findProcessingCourseRegistrationRequestByPersonId();

    Map<Integer, CourseRegistrationRequestDto> findCourseRegistrationRequestsByRequestIdIn(List<Integer> requestIds);
}
