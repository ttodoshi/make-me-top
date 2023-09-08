package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.courserequest.CreateCourseRegistrationRequestDto;
import org.example.exception.classes.requestEX.RequestNotFoundException;
import org.example.exception.classes.requestEX.StatusNotFoundException;
import org.example.model.Keeper;
import org.example.model.Person;
import org.example.model.courserequest.CourseRegistrationRequest;
import org.example.model.courserequest.CourseRegistrationRequestKeeper;
import org.example.model.courserequest.CourseRegistrationRequestKeeperStatusType;
import org.example.model.courserequest.CourseRegistrationRequestStatusType;
import org.example.repository.KeeperRepository;
import org.example.repository.courserequest.CourseRegistrationRequestKeeperRepository;
import org.example.repository.courserequest.CourseRegistrationRequestKeeperStatusRepository;
import org.example.repository.courserequest.CourseRegistrationRequestRepository;
import org.example.repository.courserequest.CourseRegistrationRequestStatusRepository;
import org.example.service.validator.CourseRegistrationRequestValidatorService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CourseRegistrationRequestService {
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final CourseRegistrationRequestStatusRepository courseRegistrationRequestStatusRepository;
    private final CourseRegistrationRequestKeeperRepository courseRegistrationRequestKeeperRepository;
    private final CourseRegistrationRequestKeeperStatusRepository courseRegistrationRequestKeeperStatusRepository;
    private final KeeperRepository keeperRepository;

    private final CourseRegistrationRequestValidatorService courseRegistrationRequestValidatorService;

    @Transactional
    public CourseRegistrationRequest sendRequest(CreateCourseRegistrationRequestDto request) {
        Integer authenticatedPersonId = getAuthenticatedPersonId();
        courseRegistrationRequestValidatorService.validateSendRequest(authenticatedPersonId, request);
        if (request.getKeeperId() == null)
            return sendRequestToAllKeepers(authenticatedPersonId, request.getCourseId());
        return sendRequestToKeeper(authenticatedPersonId, request);
    }

    private CourseRegistrationRequest sendRequestToAllKeepers(Integer personId, Integer courseId) {
        CourseRegistrationRequest request = sendRequest(personId, courseId);
        Integer processingStatusId = getRequestKeeperProcessingStatusId();
        for (Keeper keeper : keeperRepository.findKeepersByCourseId(courseId)) {
            courseRegistrationRequestKeeperRepository.save(
                    new CourseRegistrationRequestKeeper(
                            request.getRequestId(),
                            keeper.getKeeperId(),
                            processingStatusId
                    )
            );
        }
        return request;
    }

    private CourseRegistrationRequest sendRequestToKeeper(Integer personId, CreateCourseRegistrationRequestDto request) {
        CourseRegistrationRequest sentRequest = sendRequest(personId, request.getCourseId());
        Integer processingStatusId = getRequestKeeperProcessingStatusId();
        courseRegistrationRequestKeeperRepository.save(
                new CourseRegistrationRequestKeeper(
                        sentRequest.getRequestId(),
                        request.getKeeperId(),
                        processingStatusId
                )
        );
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

    private Integer getRequestKeeperProcessingStatusId() {
        return courseRegistrationRequestKeeperStatusRepository
                .findCourseRegistrationRequestKeeperStatusByStatus(CourseRegistrationRequestKeeperStatusType.PROCESSING)
                .orElseThrow(() -> new StatusNotFoundException(CourseRegistrationRequestKeeperStatusType.PROCESSING))
                .getStatusId();
    }

    private Integer getAuthenticatedPersonId() {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }

    @Transactional
    public Map<String, String> cancelRequest(Integer requestId) {
        CourseRegistrationRequest request = courseRegistrationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException(requestId));
        courseRegistrationRequestValidatorService.validateCancelRequest(request);
        courseRegistrationRequestRepository.deleteById(requestId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Вы отменили запрос на прохождение курса " + request.getCourseId());
        return response;
    }
}
