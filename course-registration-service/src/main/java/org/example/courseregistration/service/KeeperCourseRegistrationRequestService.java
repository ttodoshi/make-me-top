package org.example.courseregistration.service;

import org.example.courseregistration.dto.courserequest.ApprovedRequestDto;
import org.example.courseregistration.dto.courserequest.CourseRegistrationRequestKeeperDto;
import org.example.courseregistration.dto.courserequest.CreateKeeperRejectionDto;

import java.util.List;

public interface KeeperCourseRegistrationRequestService {
    CourseRegistrationRequestKeeperDto approveRequest(String authorizationHeader, Long authenticatedPersonId, Long requestId);

    Long rejectRequest(String authorizationHeader, Long authenticatedPersonId, Long requestId, CreateKeeperRejectionDto rejection);

    List<ApprovedRequestDto> getApprovedRequests(String authorizationHeader, Long authenticatedPersonId, List<Long> keeperIds);

    Long startTeaching(String authorizationHeader, Long authenticatedPersonId, Long courseId);
}
