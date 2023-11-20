package org.example.courseregistration.service;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.dto.courserequest.ApprovedRequestDto;
import org.example.courseregistration.dto.courserequest.CourseRegistrationRequestReplyDto;
import org.example.person.dto.event.ExplorerCreateEvent;
import org.example.courseregistration.exception.classes.keeper.KeeperNotFoundException;
import org.example.courseregistration.exception.classes.request.NoApprovedRequestsFoundException;
import org.example.courseregistration.exception.classes.request.RequestNotFoundException;
import org.example.courseregistration.exception.classes.request.StatusNotFoundException;
import org.example.courseregistration.model.CourseRegistrationRequest;
import org.example.courseregistration.model.CourseRegistrationRequestKeeper;
import org.example.courseregistration.model.CourseRegistrationRequestKeeperStatusType;
import org.example.courseregistration.model.CourseRegistrationRequestStatusType;
import org.example.courseregistration.repository.*;
import org.example.courseregistration.service.validator.KeeperCourseRegistrationRequestValidatorService;
import org.example.grpc.ExplorerGroupsService;
import org.example.grpc.KeepersService;
import org.example.grpc.PeopleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
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
            courseRegistrationRequestKeeperService.closeRequestToOtherKeepers(request);
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

    @Transactional(readOnly = true)
    public List<ApprovedRequestDto> getApprovedRequests(List<Integer> keeperIds) {
        Map<Integer, KeepersService.Keeper> keepers = keeperRepository.findKeepersByKeeperIdIn(keeperIds);
        keeperCourseRegistrationRequestValidatorService.validateGetApprovedRequests(personService.getAuthenticatedPersonId(), keepers);
        return courseRegistrationRequestRepository.findApprovedKeeperRequestsByKeeperIdIn(keeperIds)
                .stream()
                .map(r -> new ApprovedRequestDto(
                        r.getRequestId(),
                        r.getRequest().getCourseId(),
                        r.getRequest().getPersonId(),
                        r.getRequest().getStatusId(),
                        r.getKeeperId(),
                        r.getResponseDate()
                )).collect(Collectors.toList());
    }

    @Transactional
    public List<CourseRegistrationRequest> startTeaching(Integer courseId) {
        PeopleService.Person authenticatedPerson = personService.getAuthenticatedPerson();
        keeperCourseRegistrationRequestValidatorService.validateStartTeachingRequest(authenticatedPerson.getPersonId());
        Integer acceptedStatusId = courseRegistrationRequestStatusRepository
                .findCourseRegistrationRequestStatusByStatus(CourseRegistrationRequestStatusType.ACCEPTED)
                .orElseThrow(() -> new StatusNotFoundException(CourseRegistrationRequestStatusType.ACCEPTED))
                .getStatusId();
        KeepersService.Keeper keeper = keeperRepository
                .findKeeperByPersonIdAndCourseId(authenticatedPerson.getPersonId(), courseId)
                .orElseThrow(KeeperNotFoundException::new);
        List<CourseRegistrationRequest> approvedRequests = courseRegistrationRequestRepository
                .findApprovedRequestsByKeeperId(keeper.getKeeperId());
        if (approvedRequests.isEmpty())
            throw new NoApprovedRequestsFoundException();
        Integer groupId = explorerGroupRepository.save(
                ExplorerGroupsService.CreateGroupRequest.newBuilder()
                        .setCourseId(courseId)
                        .setKeeperId(keeper.getKeeperId())
                        .build()
        ).getGroupId();
        return approvedRequests.stream()
                .limit(authenticatedPerson.getMaxExplorers())
                .peek(r -> {
                    addExplorer(r.getPersonId(), groupId);
                    r.setStatusId(acceptedStatusId);
                })
                .collect(Collectors.toList());
    }
}
