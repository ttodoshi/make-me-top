package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.courserequest.CreateCourseRegistrationRequestDto;
import org.example.dto.message.MessageDto;
import org.example.exception.classes.requestEX.RequestNotFoundException;
import org.example.exception.classes.requestEX.StatusNotFoundException;
import org.example.model.CourseRegistrationRequest;
import org.example.model.CourseRegistrationRequestKeeper;
import org.example.model.CourseRegistrationRequestKeeperStatusType;
import org.example.model.CourseRegistrationRequestStatusType;
import org.example.repository.CourseRegistrationRequestKeeperRepository;
import org.example.repository.CourseRegistrationRequestKeeperStatusRepository;
import org.example.repository.CourseRegistrationRequestRepository;
import org.example.repository.CourseRegistrationRequestStatusRepository;
import org.example.service.validator.ExplorerCourseRegistrationRequestValidatorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExplorerCourseRegistrationRequestService {
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final CourseRegistrationRequestStatusRepository courseRegistrationRequestStatusRepository;
    private final CourseRegistrationRequestKeeperRepository courseRegistrationRequestKeeperRepository;
    private final CourseRegistrationRequestKeeperStatusRepository courseRegistrationRequestKeeperStatusRepository;

    private final PersonService personService;
    private final ExplorerCourseRegistrationRequestValidatorService explorerCourseRegistrationRequestValidatorService;

    @Transactional
    public CourseRegistrationRequest sendRequest(CreateCourseRegistrationRequestDto request) {
        Integer authenticatedPersonId = personService.getAuthenticatedPersonId();
        explorerCourseRegistrationRequestValidatorService.validateSendRequest(authenticatedPersonId, request);
        return sendRequestToKeepers(authenticatedPersonId, request);
    }

    private CourseRegistrationRequest sendRequestToKeepers(Integer personId, CreateCourseRegistrationRequestDto request) {
        CourseRegistrationRequest sentRequest = sendRequest(personId, request.getCourseId());
        Integer processingStatusId = getRequestKeeperProcessingStatus();
        request.getKeeperIds().forEach(kId ->
                courseRegistrationRequestKeeperRepository.save(
                        new CourseRegistrationRequestKeeper(
                                sentRequest.getRequestId(),
                                kId,
                                processingStatusId
                        )
                ));
        return sentRequest;
    }

    private CourseRegistrationRequest sendRequest(Integer personId, Integer courseId) {
        Integer processingStatusId = courseRegistrationRequestStatusRepository
                .findCourseRegistrationRequestStatusByStatus(CourseRegistrationRequestStatusType.PROCESSING)
                .orElseThrow(() -> new StatusNotFoundException(CourseRegistrationRequestStatusType.PROCESSING))
                .getStatusId();
        CourseRegistrationRequest courseRegistrationRequest = new CourseRegistrationRequest(
                courseId,
                personId,
                processingStatusId
        );
        return courseRegistrationRequestRepository.save(courseRegistrationRequest);
    }

    private Integer getRequestKeeperProcessingStatus() {
        return courseRegistrationRequestKeeperStatusRepository
                .findCourseRegistrationRequestKeeperStatusByStatus(CourseRegistrationRequestKeeperStatusType.PROCESSING)
                .orElseThrow(() -> new StatusNotFoundException(CourseRegistrationRequestKeeperStatusType.PROCESSING))
                .getStatusId();
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
