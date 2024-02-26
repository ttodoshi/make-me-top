package org.example.courseregistration.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.courseregistration.dto.courserequest.ApprovedRequestDto;
import org.example.courseregistration.dto.courserequest.CourseRegistrationRequestKeeperDto;
import org.example.courseregistration.dto.courserequest.CreateKeeperRejectionDto;
import org.example.courseregistration.exception.courserequest.NoApprovedRequestsFoundException;
import org.example.courseregistration.mapper.CourseRegistrationRequestMapper;
import org.example.courseregistration.model.*;
import org.example.courseregistration.repository.CourseRegistrationRequestKeeperRepository;
import org.example.courseregistration.repository.CourseRegistrationRequestRepository;
import org.example.courseregistration.repository.KeeperRejectionRepository;
import org.example.courseregistration.service.*;
import org.example.courseregistration.service.validator.KeeperCourseRegistrationRequestValidatorService;
import org.example.grpc.ExplorerGroupsService;
import org.example.grpc.KeepersService;
import org.example.grpc.PeopleService;
import org.example.person.dto.event.ExplorerCreateEvent;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeeperCourseRegistrationRequestServiceImpl implements KeeperCourseRegistrationRequestService {
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final CourseRegistrationRequestKeeperRepository courseRegistrationRequestKeeperRepository;
    private final KeeperRejectionRepository keeperRejectionRepository;

    private final ExplorerGroupService explorerGroupService;
    private final ExplorerService explorerService;
    private final KeeperService keeperService;
    private final PersonService personService;
    private final CourseRegistrationRequestService courseRegistrationRequestService;
    private final CourseRegistrationRequestStatusService courseRegistrationRequestStatusService;
    private final CourseRegistrationRequestKeeperService courseRegistrationRequestKeeperService;
    private final CourseRegistrationRequestKeeperStatusService courseRegistrationRequestKeeperStatusService;

    private final KeeperCourseRegistrationRequestValidatorService keeperCourseRegistrationRequestValidatorService;

    private final ModelMapper mapper;

    @Override
    @Transactional
    public CourseRegistrationRequestKeeperDto approveRequest(String authorizationHeader, Long authenticatedPersonId, Long requestId) {
        CourseRegistrationRequest request = courseRegistrationRequestService
                .findCourseRegistrationRequestById(requestId);

        keeperCourseRegistrationRequestValidatorService.validateApproveRequest(request);

        CourseRegistrationRequestKeeper keeperResponse = courseRegistrationRequestKeeperService
                .findCourseRegistrationRequestKeeperForPerson(
                        authorizationHeader, authenticatedPersonId, request
                );

        courseRegistrationRequestKeeperService.closeRequestForKeepers(request);
        updateRequestStatusToApproved(request, keeperResponse);

        return mapper.map(keeperResponse, CourseRegistrationRequestKeeperDto.class);
    }

    private void updateRequestStatusToApproved(CourseRegistrationRequest request, CourseRegistrationRequestKeeper keeperResponse) {
        courseRegistrationRequestStatusService.updateCourseRegistrationRequestStatus(
                request, CourseRegistrationRequestStatusType.APPROVED
        );
        courseRegistrationRequestKeeperStatusService.updateCourseRegistrationRequestKeeperStatus(
                keeperResponse, CourseRegistrationRequestKeeperStatusType.APPROVED
        );
    }

    @Override
    @Transactional
    public Long rejectRequest(String authorizationHeader, Long authenticatedPersonId, Long requestId, CreateKeeperRejectionDto rejection) {
        CourseRegistrationRequest request = courseRegistrationRequestService
                .findCourseRegistrationRequestById(requestId);
        CourseRegistrationRequestKeeper keeperResponse = courseRegistrationRequestKeeperService.findCourseRegistrationRequestKeeperForPerson(
                authorizationHeader, authenticatedPersonId, request
        );

        keeperCourseRegistrationRequestValidatorService.validateRejectRequest(request, keeperResponse, rejection);

        courseRegistrationRequestKeeperStatusService.updateCourseRegistrationRequestKeeperStatus(
                keeperResponse, CourseRegistrationRequestKeeperStatusType.REJECTED
        );

        return keeperRejectionRepository.save(
                new KeeperRejection(keeperResponse, rejection.getReasonId())
        ).getResponseId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApprovedRequestDto> getApprovedRequests(String authorizationHeader, Long authenticatedPersonId, List<Long> keeperIds) {
        Map<Long, KeepersService.Keeper> keepers = keeperService
                .findKeepersByKeeperIdIn(authorizationHeader, keeperIds);
        keeperCourseRegistrationRequestValidatorService.validateGetApprovedRequests(authenticatedPersonId, keepers);

        return courseRegistrationRequestKeeperRepository.findApprovedKeeperRequestsByKeeperIdIn(keeperIds)
                .stream()
                .map(CourseRegistrationRequestMapper::mapCourseRegistrationRequestKeeperToApprovedRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long startTeaching(String authorizationHeader, Long authenticatedPersonId, Long courseId) {
        keeperCourseRegistrationRequestValidatorService
                .validateStartTeachingRequest(authorizationHeader, authenticatedPersonId);

        KeepersService.Keeper keeper = keeperService.findKeeperByPersonIdAndCourseId(
                authorizationHeader, authenticatedPersonId, courseId
        );
        List<CourseRegistrationRequest> approvedRequests = courseRegistrationRequestRepository
                .findApprovedRequestsByKeeperId(keeper.getKeeperId());

        return createExplorerGroup(
                authorizationHeader, authenticatedPersonId, courseId, keeper.getKeeperId(), approvedRequests
        );
    }

    private Long createExplorerGroup(String authorizationHeader, Long authenticatedPersonId, Long courseId, Long keeperId, List<CourseRegistrationRequest> approvedRequests) {
        if (approvedRequests.isEmpty()) {
            log.warn("no approved requests found");
            throw new NoApprovedRequestsFoundException();
        }

        Long createdGroupId = explorerGroupService.save(
                authorizationHeader,
                ExplorerGroupsService.CreateGroupRequest.newBuilder()
                        .setCourseId(courseId)
                        .setKeeperId(keeperId)
                        .build()
        ).getGroupId();
        createExplorers(authorizationHeader, authenticatedPersonId, approvedRequests, createdGroupId);
        return createdGroupId;
    }

    private void createExplorers(String authorizationHeader, Long authenticatedPersonId, List<CourseRegistrationRequest> approvedRequests, Long groupId) {
        PeopleService.Person person = personService.findPersonById(authorizationHeader, authenticatedPersonId);
        Long acceptedStatusId = courseRegistrationRequestStatusService
                .findCourseRegistrationRequestStatusByStatus(CourseRegistrationRequestStatusType.ACCEPTED)
                .getStatusId();

        approvedRequests.stream()
                .limit(person.getMaxExplorers())
                .forEach(r -> {
                    r.setStatusId(acceptedStatusId);
                    explorerService.save(
                            new ExplorerCreateEvent(r.getPersonId(), groupId)
                    );
                });
    }
}
