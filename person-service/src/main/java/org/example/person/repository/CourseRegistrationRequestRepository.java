package org.example.person.repository;

import org.example.person.dto.courserequest.ApprovedRequestDto;
import org.example.person.dto.courserequest.CourseRegistrationRequestDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CourseRegistrationRequestRepository {
    Optional<CourseRegistrationRequestDto> findProcessingCourseRegistrationRequestByPersonId();

    Map<Long, CourseRegistrationRequestDto> findCourseRegistrationRequestsByRequestIdIn(List<Long> requestIds);

    List<ApprovedRequestDto> getApprovedCourseRegistrationRequests(List<Long> keeperIds);
}
