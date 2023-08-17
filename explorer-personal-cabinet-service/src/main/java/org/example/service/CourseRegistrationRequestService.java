package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.courseregistration.CreateCourseRegistrationRequest;
import org.example.exception.classes.requestEX.PersonIsNotPersonInRequestException;
import org.example.exception.classes.requestEX.RequestNotFoundException;
import org.example.exception.classes.requestEX.StatusNotFoundException;
import org.example.model.Person;
import org.example.model.courserequest.CourseRegistrationRequest;
import org.example.model.courserequest.CourseRegistrationRequestStatusType;
import org.example.repository.courseregistration.CourseRegistrationRequestRepository;
import org.example.repository.courseregistration.CourseRegistrationRequestStatusRepository;
import org.example.service.validator.CourseRegistrationRequestValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CourseRegistrationRequestService {
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final CourseRegistrationRequestStatusRepository courseRegistrationRequestStatusRepository;

    private final CourseRegistrationRequestValidatorService courseRegistrationRequestValidatorService;

    private final ModelMapper mapper;

    public CourseRegistrationRequest sendRequest(CreateCourseRegistrationRequest request) {
        Integer authenticatedPersonId = getAuthenticatedPersonId();
        courseRegistrationRequestValidatorService.validateSendRequest(authenticatedPersonId, request);
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

    public Map<String, String> cancelRequest(Integer requestId) {
        CourseRegistrationRequest request = courseRegistrationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException(requestId));
        if (!request.getPersonId().equals(getAuthenticatedPersonId()))
            throw new PersonIsNotPersonInRequestException();
        request.setStatusId(
                courseRegistrationRequestStatusRepository
                        .findCourseRegistrationRequestStatusByStatus(CourseRegistrationRequestStatusType.DENIED)
                        .orElseThrow(() -> new StatusNotFoundException(CourseRegistrationRequestStatusType.DENIED))
                        .getStatusId()
        );
        courseRegistrationRequestRepository.save(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Вы отменили запрос на прохождение курса " + request.getCourseId());
        return response;
    }
}
