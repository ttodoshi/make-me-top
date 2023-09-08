package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.courserequest.CourseRegistrationRequestReplyDto;
import org.example.exception.classes.keeperEX.KeeperNotFoundException;
import org.example.exception.classes.requestEX.NoApprovedRequestsFoundException;
import org.example.exception.classes.requestEX.RequestNotFoundException;
import org.example.exception.classes.requestEX.StatusNotFoundException;
import org.example.model.Explorer;
import org.example.model.ExplorerGroup;
import org.example.model.Keeper;
import org.example.model.Person;
import org.example.model.courserequest.CourseRegistrationRequest;
import org.example.model.courserequest.CourseRegistrationRequestKeeper;
import org.example.model.courserequest.CourseRegistrationRequestKeeperStatusType;
import org.example.model.courserequest.CourseRegistrationRequestStatusType;
import org.example.repository.ExplorerGroupRepository;
import org.example.repository.ExplorerRepository;
import org.example.repository.KeeperRepository;
import org.example.repository.courserequest.CourseRegistrationRequestRepository;
import org.example.repository.courserequest.CourseRegistrationRequestStatusRepository;
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
    private final ExplorerGroupRepository explorerGroupRepository;
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;

    private final CourseRegistrationRequestKeeperService courseRegistrationRequestKeeperService;

    private final KafkaTemplate<String, Integer> kafkaTemplate;
    private final CourseRegistrationRequestValidatorService courseRegistrationRequestValidatorService;

    @Transactional
    public CourseRegistrationRequestKeeper replyToRequest(Integer requestId, CourseRegistrationRequestReplyDto requestReply) {
        CourseRegistrationRequest request = courseRegistrationRequestRepository
                .findById(requestId).orElseThrow(() -> new RequestNotFoundException(requestId));
        CourseRegistrationRequestKeeper keeperResponse = courseRegistrationRequestKeeperService
                .findCourseRegistrationRequestForAuthenticatedKeeper(request);
        courseRegistrationRequestValidatorService.validateRequest(request);
        return sendKeeperResponse(request, keeperResponse, requestReply.getApproved());
    }

    private CourseRegistrationRequestKeeper sendKeeperResponse(CourseRegistrationRequest request, CourseRegistrationRequestKeeper keeperResponse, boolean approved) {
        CourseRegistrationRequestKeeperStatusType keeperResponseStatus;
        if (approved) {
            keeperResponseStatus = CourseRegistrationRequestKeeperStatusType.APPROVED;
            changeRequestStatusToApproved(request);
            courseRegistrationRequestKeeperService.closeRequestToOtherKeepersOnCourse(request);
        } else {
            keeperResponseStatus = CourseRegistrationRequestKeeperStatusType.REJECTED;
            if (courseRegistrationRequestKeeperService.isRequestPersonallyForKeeper(request))
                courseRegistrationRequestKeeperService.openRequestToOtherKeepersOnCourse(request);
        }
        return courseRegistrationRequestKeeperService
                .saveKeeperResponseWithStatus(keeperResponse, keeperResponseStatus);
    }

    private void changeRequestStatusToApproved(CourseRegistrationRequest request) {
        Integer requestStatusId = courseRegistrationRequestStatusRepository
                .findCourseRegistrationRequestStatusByStatus(CourseRegistrationRequestStatusType.APPROVED)
                .orElseThrow(() -> new StatusNotFoundException(CourseRegistrationRequestStatusType.APPROVED)).getStatusId();
        request.setStatusId(requestStatusId);
        courseRegistrationRequestRepository.save(request);
    }

    private Integer getAuthenticatedPersonId() {
        final Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }

    private void addExplorer(Integer personId, Integer groupId) {
        explorerRepository.save(
                new Explorer(personId, groupId)
        );
    }

    private void sendGalaxyCacheRefreshMessage(Integer courseId) {
        kafkaTemplate.send("galaxyCacheTopic", courseId);
    }

    @Transactional
    public List<CourseRegistrationRequest> getApprovedRequests(Integer courseId) {
        courseRegistrationRequestValidatorService.validateGetApprovedRequests(getAuthenticatedPersonId(), courseId);
        return courseRegistrationRequestRepository.findApprovedRequestsByKeeperPersonIdAndCourseId(getAuthenticatedPersonId(), courseId);
    }

    @Transactional
    public List<CourseRegistrationRequest> startTeaching(Integer courseId) {
        final Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        courseRegistrationRequestValidatorService.validateStartTeachingRequest(authenticatedPerson.getPersonId());
        Integer acceptedStatusId = courseRegistrationRequestStatusRepository
                .findCourseRegistrationRequestStatusByStatus(CourseRegistrationRequestStatusType.ACCEPTED)
                .orElseThrow(() -> new StatusNotFoundException(CourseRegistrationRequestStatusType.ACCEPTED))
                .getStatusId();
        List<CourseRegistrationRequest> approvedRequests = getApprovedRequests(courseId);
        if (approvedRequests.isEmpty())
            throw new NoApprovedRequestsFoundException();
        Keeper keeper = keeperRepository
                .findKeeperByPersonIdAndCourseId(authenticatedPerson.getPersonId(), courseId)
                .orElseThrow(KeeperNotFoundException::new);
        Integer groupId = explorerGroupRepository.save(
                new ExplorerGroup(courseId, keeper.getKeeperId())
        ).getGroupId();
        sendGalaxyCacheRefreshMessage(courseId);
        return approvedRequests.stream()
                .limit(authenticatedPerson.getMaxExplorers())
                .peek(r -> {
                    addExplorer(r.getPersonId(), groupId);
                    r.setStatusId(acceptedStatusId);
                })
                .collect(Collectors.toList());
    }
}
