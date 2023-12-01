package org.example.courseregistration.service;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.dto.courserequest.ApprovedRequestDto;
import org.example.courseregistration.dto.courserequest.CourseRegistrationRequestReplyDto;
import org.example.courseregistration.exception.classes.keeper.KeeperNotFoundException;
import org.example.courseregistration.exception.classes.request.NoApprovedRequestsFoundException;
import org.example.courseregistration.exception.classes.request.RequestNotFoundException;
import org.example.courseregistration.model.CourseRegistrationRequest;
import org.example.courseregistration.model.CourseRegistrationRequestKeeper;
import org.example.courseregistration.model.CourseRegistrationRequestKeeperStatusType;
import org.example.courseregistration.model.CourseRegistrationRequestStatusType;
import org.example.courseregistration.repository.CourseRegistrationRequestRepository;
import org.example.courseregistration.repository.ExplorerGroupRepository;
import org.example.courseregistration.repository.ExplorerRepository;
import org.example.courseregistration.repository.KeeperRepository;
import org.example.courseregistration.service.validator.KeeperCourseRegistrationRequestValidatorService;
import org.example.grpc.ExplorerGroupsService;
import org.example.grpc.KeepersService;
import org.example.grpc.PeopleService;
import org.example.person.dto.event.ExplorerCreateEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeeperCourseRegistrationRequestService {
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final ExplorerGroupRepository explorerGroupRepository;
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;

    private final PersonService personService;
    private final CourseRegistrationRequestStatusService courseRegistrationRequestStatusService;
    private final CourseRegistrationRequestKeeperService courseRegistrationRequestKeeperService;
    private final CourseRegistrationRequestKeeperStatusService courseRegistrationRequestKeeperStatusService;

    private final KeeperCourseRegistrationRequestValidatorService keeperCourseRegistrationRequestValidatorService;

    @Transactional
    public CourseRegistrationRequestKeeper replyToRequest(Long requestId, CourseRegistrationRequestReplyDto requestReply) {
        CourseRegistrationRequest request = courseRegistrationRequestRepository
                .findById(requestId).orElseThrow(() -> new RequestNotFoundException(requestId));
        keeperCourseRegistrationRequestValidatorService.validateReply(request);

        CourseRegistrationRequestKeeper keeperResponse = courseRegistrationRequestKeeperService
                .findCourseRegistrationRequestKeeperForPerson(
                        personService.getAuthenticatedPersonId(),
                        request
                );

        return sendKeeperResponse(request, keeperResponse, requestReply.getApproved());
    }

    private CourseRegistrationRequestKeeper sendKeeperResponse(CourseRegistrationRequest request, CourseRegistrationRequestKeeper keeperResponse, boolean approved) {
        CourseRegistrationRequestKeeperStatusType keeperResponseStatus;
        if (approved) {
            keeperResponseStatus = CourseRegistrationRequestKeeperStatusType.APPROVED;

            request.setStatusId(
                    courseRegistrationRequestStatusService
                            .findCourseRegistrationRequestStatusByStatus(CourseRegistrationRequestStatusType.APPROVED)
                            .getStatusId()
            );

            courseRegistrationRequestKeeperService.closeRequestForKeepers(request);
        } else {
            keeperResponseStatus = CourseRegistrationRequestKeeperStatusType.REJECTED;
        }

        keeperResponse.setStatusId(
                courseRegistrationRequestKeeperStatusService
                        .findCourseRegistrationRequestKeeperStatusByStatus(keeperResponseStatus)
                        .getStatusId()
        );
        return keeperResponse;
    }

    @Transactional(readOnly = true)
    public List<ApprovedRequestDto> getApprovedRequests(List<Long> keeperIds) {
        Map<Long, KeepersService.Keeper> keepers = keeperRepository.findKeepersByKeeperIdIn(keeperIds);
        keeperCourseRegistrationRequestValidatorService
                .validateGetApprovedRequests(personService.getAuthenticatedPersonId(), keepers);

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
    public List<CourseRegistrationRequest> startTeaching(Long courseId) {
        PeopleService.Person authenticatedPerson = personService.getAuthenticatedPerson();

        keeperCourseRegistrationRequestValidatorService
                .validateStartTeachingRequest(authenticatedPerson.getPersonId());

        KeepersService.Keeper keeper = keeperRepository
                .findKeeperByPersonIdAndCourseId(authenticatedPerson.getPersonId(), courseId)
                .orElseThrow(KeeperNotFoundException::new);

        List<CourseRegistrationRequest> approvedRequests = courseRegistrationRequestRepository
                .findApprovedRequestsByKeeperId(keeper.getKeeperId());
        if (approvedRequests.isEmpty())
            throw new NoApprovedRequestsFoundException();

        Long acceptedStatusId = courseRegistrationRequestStatusService
                .findCourseRegistrationRequestStatusByStatus(CourseRegistrationRequestStatusType.ACCEPTED)
                .getStatusId();
        Long createdGroupId = explorerGroupRepository.save(
                ExplorerGroupsService.CreateGroupRequest.newBuilder()
                        .setCourseId(courseId)
                        .setKeeperId(keeper.getKeeperId())
                        .build()
        ).getGroupId();

        return approvedRequests.stream()
                .limit(authenticatedPerson.getMaxExplorers())
                .peek(r -> r.setStatusId(acceptedStatusId))
                .peek(r -> explorerRepository.save(
                        new ExplorerCreateEvent(r.getPersonId(), createdGroupId))
                ).collect(Collectors.toList());
    }
}
