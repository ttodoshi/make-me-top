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
        Long authenticatedPersonId = personService.getAuthenticatedPersonId();
        explorerCourseRegistrationRequestValidatorService.validateSendRequest(authenticatedPersonId, request);
        return sendRequestToKeepers(authenticatedPersonId, request);
    }

    private CourseRegistrationRequest sendRequestToKeepers(Long personId, CreateCourseRegistrationRequestDto request) {
        CourseRegistrationRequest sentRequest = createRequest(personId, request.getCourseId());
        Long keeperProcessingStatusId = courseRegistrationRequestKeeperStatusService
                .findCourseRegistrationRequestKeeperStatusByStatus(CourseRegistrationRequestKeeperStatusType.PROCESSING)
                .getStatusId();

        request.getKeeperIds().forEach(kId ->
                courseRegistrationRequestKeeperRepository.save(
                        new CourseRegistrationRequestKeeper(
                                sentRequest.getRequestId(),
                                kId,
                                keeperProcessingStatusId
                        )
                ));
        return sentRequest;
    }

    private CourseRegistrationRequest createRequest(Long personId, Long courseId) {
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
    public MessageDto cancelRequest(Long requestId) {
        CourseRegistrationRequest request = courseRegistrationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException(requestId));
        explorerCourseRegistrationRequestValidatorService.validateCancelRequest(request);
        courseRegistrationRequestRepository.deleteById(requestId);
        return new MessageDto("Вы отменили запрос на прохождение курса " + request.getCourseId());
    }
}
