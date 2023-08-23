package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.courserequest.CourseRegistrationRequestReply;
import org.example.dto.courserequest.KeeperRejectionDTO;
import org.example.exception.classes.keeperEX.DifferentKeeperException;
import org.example.exception.classes.keeperEX.KeeperNotFoundException;
import org.example.exception.classes.requestEX.RequestNotFoundException;
import org.example.exception.classes.requestEX.StatusNotFoundException;
import org.example.model.Explorer;
import org.example.model.Keeper;
import org.example.model.Person;
import org.example.model.courserequest.*;
import org.example.repository.ExplorerRepository;
import org.example.repository.KeeperRepository;
import org.example.repository.courserequest.*;
import org.example.service.validator.CourseRegistrationRequestValidatorService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseRegistrationRequestService {
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final CourseRegistrationRequestStatusRepository courseRegistrationRequestStatusRepository;
    private final KeeperRejectionRepository keeperRejectionRepository;
    private final RejectionReasonRepository rejectionReasonRepository;
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;
    private final CourseRegistrationRequestKeeperRepository courseRegistrationRequestKeeperRepository;
    private final CourseRegistrationRequestKeeperStatusRepository courseRegistrationRequestKeeperStatusRepository;

    private final KafkaTemplate<String, Integer> kafkaTemplate;
    private final CourseRegistrationRequestValidatorService courseRegistrationRequestValidatorService;

    @Transactional
    public CourseRegistrationRequestKeeper replyToRequest(Integer requestId, CourseRegistrationRequestReply requestReply) {
        CourseRegistrationRequest request = courseRegistrationRequestRepository
                .findById(requestId).orElseThrow(() -> new RequestNotFoundException(requestId));
        CourseRegistrationRequestKeeper keeperResponse = findCourseRegistrationRequestForAuthenticatedKeeper(request);
        courseRegistrationRequestValidatorService.validateRequest(request);
        return sendKeeperResponse(request, keeperResponse, requestReply.getApproved());
    }

    private CourseRegistrationRequestKeeper findCourseRegistrationRequestForAuthenticatedKeeper(CourseRegistrationRequest request) {
        Keeper keeper = keeperRepository
                .findKeeperByPersonIdAndCourseId(
                        getAuthenticatedPersonId(), request.getCourseId()
                ).orElseThrow(KeeperNotFoundException::new);
        return courseRegistrationRequestKeeperRepository
                .findCourseRegistrationRequestKeeperByRequestIdAndKeeperId(
                        request.getRequestId(),
                        keeper.getKeeperId()
                ).orElseThrow(DifferentKeeperException::new);
    }

    private CourseRegistrationRequestKeeper sendKeeperResponse(CourseRegistrationRequest request, CourseRegistrationRequestKeeper keeperResponse, boolean approved) {
        CourseRegistrationRequestKeeperStatusType keeperResponseStatus;
        if (approved) {
            keeperResponseStatus = CourseRegistrationRequestKeeperStatusType.APPROVED;
            changeRequestStatusToApproved(request);
            closeRequestToOtherKeepers(request);
        } else {
            keeperResponseStatus = CourseRegistrationRequestKeeperStatusType.REJECTED;
            if (requestIsPersonallyForKeeper(request))
                openRequestToOtherKeepersOnCourse(request);
        }
        Integer keeperResponseStatusId = courseRegistrationRequestKeeperStatusRepository
                .findCourseRegistrationRequestKeeperStatusByStatus(keeperResponseStatus)
                .orElseThrow(() -> new StatusNotFoundException(keeperResponseStatus)).getStatusId();
        keeperResponse.setStatusId(keeperResponseStatusId);
        return courseRegistrationRequestKeeperRepository.save(keeperResponse);
    }

    private void changeRequestStatusToApproved(CourseRegistrationRequest request) {
        Integer requestStatusId = courseRegistrationRequestStatusRepository
                .findCourseRegistrationRequestStatusByStatus(CourseRegistrationRequestStatusType.APPROVED)
                .orElseThrow(() -> new StatusNotFoundException(CourseRegistrationRequestStatusType.APPROVED)).getStatusId();
        request.setStatusId(requestStatusId);
        courseRegistrationRequestRepository.save(request);
    }

    private void closeRequestToOtherKeepers(CourseRegistrationRequest request) {
        Integer processingStatusId = courseRegistrationRequestKeeperStatusRepository
                .findCourseRegistrationRequestKeeperStatusByStatus(CourseRegistrationRequestKeeperStatusType.PROCESSING)
                .orElseThrow(() -> new StatusNotFoundException(CourseRegistrationRequestKeeperStatusType.PROCESSING)).getStatusId();
        Integer rejectedStatusId = courseRegistrationRequestKeeperStatusRepository
                .findCourseRegistrationRequestKeeperStatusByStatus(CourseRegistrationRequestKeeperStatusType.REJECTED)
                .orElseThrow(() -> new StatusNotFoundException(CourseRegistrationRequestKeeperStatusType.REJECTED)).getStatusId();
        courseRegistrationRequestKeeperRepository
                .findCourseRegistrationRequestKeepersByRequestId(request.getRequestId())
                .forEach(
                        r -> {
                            if (r.getStatusId().equals(processingStatusId)) {
                                r.setStatusId(rejectedStatusId);
                                courseRegistrationRequestKeeperRepository.save(r);
                            }
                        }
                );
    }

    private boolean requestIsPersonallyForKeeper(CourseRegistrationRequest request) {
        List<Keeper> keepersOnCourse = keeperRepository.findKeepersByCourseId(request.getCourseId());
        Integer keepersReceivedRequestCount = courseRegistrationRequestKeeperRepository.findCourseRegistrationRequestKeepersByRequestId(request.getRequestId()).size();
        return keepersReceivedRequestCount.equals(1) && !keepersReceivedRequestCount.equals(keepersOnCourse.size());
    }

    private void openRequestToOtherKeepersOnCourse(CourseRegistrationRequest request) {
        List<Keeper> keepersOnCourse = keeperRepository.findKeepersByCourseId(request.getCourseId());
        Integer processingStatusId = courseRegistrationRequestKeeperStatusRepository
                .findCourseRegistrationRequestKeeperStatusByStatus(CourseRegistrationRequestKeeperStatusType.PROCESSING)
                .orElseThrow(() -> new StatusNotFoundException(CourseRegistrationRequestKeeperStatusType.PROCESSING)).getStatusId();
        keepersOnCourse.forEach(
                k -> {
                    if (!k.getPersonId().equals(getAuthenticatedPersonId()))
                        courseRegistrationRequestKeeperRepository.save(
                                new CourseRegistrationRequestKeeper(
                                        request.getRequestId(),
                                        k.getKeeperId(),
                                        processingStatusId
                                )
                        );
                }
        );
    }

    private Integer getAuthenticatedPersonId() {
        final Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }

    private void addExplorer(Integer personId, Integer courseId) {
        sendGalaxyCacheRefreshMessage(courseId);
        explorerRepository.save(
                Explorer.builder()
                        .personId(personId)
                        .courseId(courseId)
                        .build()
        );
    }

    private void sendGalaxyCacheRefreshMessage(Integer courseId) {
        kafkaTemplate.send("galaxyCacheTopic", courseId);
    }

    public KeeperRejection sendRejection(Integer requestId, KeeperRejectionDTO rejection) {
        courseRegistrationRequestValidatorService.validateRejectionRequest(requestId);
        return keeperRejectionRepository.save(
                KeeperRejection.builder()
                        .requestId(requestId)
                        .reasonId(rejection.getReasonId())
                        .build()
        );
    }

    public List<RejectionReason> getRejectionReasons() {
        return rejectionReasonRepository.findAll();
    }

    public List<CourseRegistrationRequest> getApprovedRequests(Integer courseId) {
        courseRegistrationRequestValidatorService.validateGetApprovedRequests(getAuthenticatedPersonId(), courseId);
        return courseRegistrationRequestRepository.findApprovedRequestsByKeeperPersonIdAndCourseId(getAuthenticatedPersonId(), courseId);
    }

    @Transactional
    public List<CourseRegistrationRequest> startTeaching(Integer courseId) {
        final Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        courseRegistrationRequestValidatorService.validateStartTeachingRequest(authenticatedPerson.getPersonId(), courseId);
        Integer acceptedStatusId = courseRegistrationRequestStatusRepository
                .findCourseRegistrationRequestStatusByStatus(CourseRegistrationRequestStatusType.ACCEPTED)
                .orElseThrow(() -> new StatusNotFoundException(CourseRegistrationRequestStatusType.ACCEPTED))
                .getStatusId();
        return getApprovedRequests(courseId).stream()
                .limit(authenticatedPerson.getMaxExplorers())
                .peek(r -> addExplorer(r.getPersonId(), r.getCourseId()))
                .peek(r -> r.setStatusId(acceptedStatusId))
                .collect(Collectors.toList());
    }
}
