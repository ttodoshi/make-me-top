package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.courseregistration.CreateCourseRegistrationRequest;
import org.example.exception.classes.requestEX.StatusNotFoundException;
import org.example.model.Person;
import org.example.model.courserequest.CourseRegistrationRequest;
import org.example.model.courserequest.CourseRegistrationRequestStatusType;
import org.example.repository.CourseRegistrationRequestRepository;
import org.example.repository.CourseRegistrationRequestStatusRepository;
import org.example.validator.CourseRegistrationRequestValidator;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseRegistrationRequestService {
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final CourseRegistrationRequestStatusRepository courseRegistrationRequestStatusRepository;

    private final CourseRegistrationRequestValidator courseRegistrationRequestValidator;

    private final ModelMapper mapper;

    public CourseRegistrationRequest sendRequest(CreateCourseRegistrationRequest request) {
        Integer authenticatedPersonId = getAuthenticatedPersonId();
        courseRegistrationRequestValidator.validateSendRequest(authenticatedPersonId, request);
        CourseRegistrationRequest courseRegistrationRequest = mapper.map(request, CourseRegistrationRequest.class);
        courseRegistrationRequest.setStatusId(
                courseRegistrationRequestStatusRepository
                        .findCourseRegistrationRequestStatusByStatus(CourseRegistrationRequestStatusType.PROCESSING)
                        .orElseThrow(() -> new StatusNotFoundException(CourseRegistrationRequestStatusType.PROCESSING)).getStatusId());
        courseRegistrationRequest.setPersonId(authenticatedPersonId);
        return courseRegistrationRequestRepository.save(courseRegistrationRequest);
    }

    private Integer getAuthenticatedPersonId() {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }
}
