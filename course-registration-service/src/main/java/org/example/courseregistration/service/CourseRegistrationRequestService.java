package org.example.courseregistration.service;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.exception.classes.request.RequestNotFoundException;
import org.example.courseregistration.repository.CourseRegistrationRequestRepository;
import org.example.courseregistration.model.CourseRegistrationRequest;
import org.springframework.kafka.annotation.KafkaListener;
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
                .findCourseRegistrationRequestByPersonIdAndStatus_NotAccepted(
                        personService.getAuthenticatedPersonId()
                ).orElseThrow(RequestNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Map<Long, CourseRegistrationRequest> findCourseRegistrationRequestsByRequestIdIn(List<Long> requestIds) {
        return courseRegistrationRequestRepository
                .findCourseRegistrationRequestsByRequestIdIn(requestIds)
                .stream()
                .collect(Collectors.toMap(
                        CourseRegistrationRequest::getRequestId,
                        r -> r
                ));
    }


    @KafkaListener(topics = "deleteCourseRegistrationRequestsTopic", containerFactory = "deleteCourseRegistrationRequestsKafkaListenerContainerFactory")
    @Transactional
    public void deleteCourseRegistrationRequestsByCourseId(Long courseId) {
        courseRegistrationRequestRepository
                .deleteCourseRegistrationRequestsByCourseId(courseId);
    }
}
