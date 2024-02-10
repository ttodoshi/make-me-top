package org.example.courseregistration.service;

import org.example.courseregistration.dto.courserequest.CreateCourseRegistrationRequestDto;
import org.example.courseregistration.dto.message.MessageDto;

public interface ExplorerCourseRegistrationRequestService {
    Long sendRequest(String authorizationHeader, Long authenticatedPersonId, CreateCourseRegistrationRequestDto request);

    MessageDto cancelRequest(Long authenticatedPersonId, Long requestId);
}
