package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.PersonDto;
import org.example.dto.courserequest.CourseRegistrationRequestReplyDto;
import org.example.dto.event.ExplorerCreateEvent;
import org.example.dto.explorer.CreateExplorerGroupDto;
import org.example.dto.keeper.KeeperDto;
import org.example.exception.classes.keeperEX.KeeperNotFoundException;
import org.example.exception.classes.requestEX.NoApprovedRequestsFoundException;
import org.example.exception.classes.requestEX.RequestNotFoundException;
import org.example.exception.classes.requestEX.StatusNotFoundException;
import org.example.model.CourseRegistrationRequest;
import org.example.model.CourseRegistrationRequestKeeper;
import org.example.model.CourseRegistrationRequestKeeperStatusType;
import org.example.model.CourseRegistrationRequestStatusType;
import org.example.repository.*;
import org.example.service.validator.KeeperCourseRegistrationRequestValidatorService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeeperCourseRegistrationRequestService {
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final CourseRegistrationRequestStatusRepository courseRegistrationRequestStatusRepository;
    private final ExplorerGroupRepository explorerGroupRepository;
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;

    private final PersonService personService;
    private final CourseRegistrationRequestKeeperService courseRegistrationRequestKeeperService;

    private final KafkaTemplate<String, Integer> kafkaTemplate;
    private final KeeperCourseRegistrationRequestValidatorService keeperCourseRegistrationRequestValidatorService;

    @Transactional
    public CourseRegistrationRequestKeeper replyToRequest(Integer requestId, CourseRegistrationRequestReplyDto requestReply) {
        CourseRegistrationRequest request = courseRegistrationRequestRepository
                .findById(requestId).orElseThrow(() -> new RequestNotFoundException(requestId));
        CourseRegistrationRequestKeeper keeperResponse = courseRegistrationRequestKeeperService
                .findCourseRegistrationRequestForAuthenticatedKeeper(request);
        keeperCourseRegistrationRequestValidatorService.validateRequest(request);
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

    private void addExplorer(Integer personId, Integer groupId) {
        explorerRepository.save(
                new ExplorerCreateEvent(personId, groupId)
        );
    }

    private void sendGalaxyCacheRefreshMessage(Integer courseId) {
        kafkaTemplate.send("galaxyCacheTopic", courseId);
    }

    @Transactional(readOnly = true)
    public List<CourseRegistrationRequest> getApprovedRequests(Integer courseId) {
        keeperCourseRegistrationRequestValidatorService.validateGetApprovedRequests(personService.getAuthenticatedPersonId(), courseId);
        Integer keeperId = keeperRepository
                .findKeeperByPersonIdAndCourseId(personService.getAuthenticatedPersonId(), courseId)
                .orElseThrow(KeeperNotFoundException::new)
                .getKeeperId();
        return courseRegistrationRequestRepository.findApprovedRequestsByKeeperId(keeperId);
    }

    @Transactional
    public List<CourseRegistrationRequest> startTeaching(Integer courseId) {
        PersonDto authenticatedPerson = personService.getAuthenticatedPerson();
        keeperCourseRegistrationRequestValidatorService.validateStartTeachingRequest(authenticatedPerson.getPersonId());
        Integer acceptedStatusId = courseRegistrationRequestStatusRepository
                .findCourseRegistrationRequestStatusByStatus(CourseRegistrationRequestStatusType.ACCEPTED)
                .orElseThrow(() -> new StatusNotFoundException(CourseRegistrationRequestStatusType.ACCEPTED))
                .getStatusId();
        List<CourseRegistrationRequest> approvedRequests = getApprovedRequests(courseId);
        if (approvedRequests.isEmpty())
            throw new NoApprovedRequestsFoundException();
        KeeperDto keeper = keeperRepository
                .findKeeperByPersonIdAndCourseId(authenticatedPerson.getPersonId(), courseId)
                .orElseThrow(KeeperNotFoundException::new);
        Integer groupId = explorerGroupRepository.save(
                new CreateExplorerGroupDto(courseId, keeper.getKeeperId())
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
