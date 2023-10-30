package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.exception.classes.requestEX.RequestNotFoundException;
import org.example.model.CourseRegistrationRequest;
import org.example.repository.CourseRegistrationRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseRegistrationRequestService {
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;

    private final PersonService personService;

    @Transactional(readOnly = true)
    public CourseRegistrationRequest findProcessingCourseRegistrationRequestByPersonId() {
        return courseRegistrationRequestRepository
                .findCourseRegistrationRequestByPersonIdAndStatus_ProcessingStatus(
                        personService.getAuthenticatedPersonId()
                ).orElseThrow(RequestNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Map<Integer, CourseRegistrationRequest> findCourseRegistrationRequestsByRequestIdIn(List<Integer> requestIds) {
        return courseRegistrationRequestRepository
                .findCourseRegistrationRequestsByRequestIdIn(requestIds)
                .stream()
                .collect(Collectors.toMap(
                        CourseRegistrationRequest::getRequestId,
                        r -> r
                ));
    }
}
