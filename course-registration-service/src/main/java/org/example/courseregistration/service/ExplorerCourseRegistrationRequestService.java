package org.example.courseregistration.service;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.dto.courserequest.CreateCourseRegistrationRequestDto;
import org.example.courseregistration.dto.message.MessageDto;
import org.example.courseregistration.exception.classes.request.RequestNotFoundException;
import org.example.courseregistration.model.CourseRegistrationRequest;
import org.example.courseregistration.model.CourseRegistrationRequestKeeper;
import org.example.courseregistration.model.CourseRegistrationRequestKeeperStatusType;
import org.example.courseregistration.model.CourseRegistrationRequestStatusType;
import org.example.courseregistration.repository.CourseRegistrationRequestKeeperRepository;
import org.example.courseregistration.repository.CourseRegistrationRequestRepository;
import org.example.courseregistration.service.validator.ExplorerCourseRegistrationRequestValidatorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExplorerCourseRegistrationRequestService {
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final CourseRegistrationRequestKeeperRepository courseRegistrationRequestKeeperRepository;
    private final CourseRegistrationRequestKeeperStatusService courseRegistrationRequestKeeperStatusService;

    private final PersonService personService;
    private final CourseRegistrationRequestStatusService courseRegistrationRequestStatusService;
    private final ExplorerCourseRegistrationRequestValidatorService explorerCourseRegistrationRequestValidatorService;

    @Transactional
    public CourseRegistrationRequest sendRequest(CreateCourseRegistrationRequestDto request) {
        Integer authenticatedPersonId = personService.getAuthenticatedPersonId();
        explorerCourseRegistrationRequestValidatorService.validateSendRequest(authenticatedPersonId, request);
        return sendRequestToKeepers(authenticatedPersonId, request);
    }

    private CourseRegistrationRequest sendRequestToKeepers(Integer personId, CreateCourseRegistrationRequestDto request) {
        CourseRegistrationRequest sentRequest = sendRequest(personId, request.getCourseId());
        request.getKeeperIds().forEach(kId ->
                courseRegistrationRequestKeeperRepository.save(
                        new CourseRegistrationRequestKeeper(
                                sentRequest.getRequestId(),
                                kId,
                                courseRegistrationRequestKeeperStatusService
                                        .findCourseRegistrationRequestKeeperStatusByStatus(CourseRegistrationRequestKeeperStatusType.PROCESSING)
                                        .getStatusId()
                        )
                ));
        return sentRequest;
    }

    private CourseRegistrationRequest sendRequest(Integer personId, Integer courseId) {
        CourseRegistrationRequest courseRegistrationRequest = new CourseRegistrationRequest(
                courseId,
                personId,
                courseRegistrationRequestStatusService
                        .findCourseRegistrationRequestStatusByStatus(CourseRegistrationRequestStatusType.PROCESSING)
                        .getStatusId()
        );
        return courseRegistrationRequestRepository.save(courseRegistrationRequest);
    }

    @Transactional
    public MessageDto cancelRequest(Integer requestId) {
        CourseRegistrationRequest request = courseRegistrationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException(requestId));
        explorerCourseRegistrationRequestValidatorService.validateCancelRequest(request);
        courseRegistrationRequestRepository.deleteById(requestId);
        return new MessageDto("Вы отменили запрос на прохождение курса " + request.getCourseId());
    }
}
