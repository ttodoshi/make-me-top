package org.example.courseregistration.service;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.dto.courserequest.ApprovedRequestDto;
import org.example.courseregistration.dto.courserequest.CourseRegistrationRequestKeeperDto;
import org.example.courseregistration.dto.courserequest.CreateKeeperRejectionDto;
import org.example.courseregistration.exception.classes.courserequest.NoApprovedRequestsFoundException;
import org.example.courseregistration.exception.classes.courserequest.RequestNotFoundException;
import org.example.courseregistration.exception.classes.keeper.DifferentKeeperException;
import org.example.courseregistration.exception.classes.keeper.KeeperNotFoundException;
import org.example.courseregistration.model.*;
import org.example.courseregistration.repository.*;
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
public class KeeperCourseRegistrationRequestService {
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final ExplorerGroupRepository explorerGroupRepository;
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;
    private final CourseRegistrationRequestKeeperRepository courseRegistrationRequestKeeperRepository;
    private final KeeperRejectionRepository keeperRejectionRepository;

    private final PersonService personService;
    private final CourseRegistrationRequestStatusService courseRegistrationRequestStatusService;
    private final CourseRegistrationRequestKeeperService courseRegistrationRequestKeeperService;
    private final CourseRegistrationRequestKeeperStatusService courseRegistrationRequestKeeperStatusService;

    private final KeeperCourseRegistrationRequestValidatorService keeperCourseRegistrationRequestValidatorService;

    private final ModelMapper mapper;

    @Transactional
    public CourseRegistrationRequestKeeperDto approveRequest(Long requestId) {
        CourseRegistrationRequest request = courseRegistrationRequestRepository
                .findById(requestId).orElseThrow(() -> new RequestNotFoundException(requestId));

        keeperCourseRegistrationRequestValidatorService.validateApproveRequest(request);

        CourseRegistrationRequestKeeper keeperResponse = courseRegistrationRequestKeeperService
                .findCourseRegistrationRequestKeeperForPerson(
                        personService.getAuthenticatedPersonId(),
                        request
                );

        courseRegistrationRequestKeeperService.closeRequestForKeepers(request);

        request.setStatusId(
                courseRegistrationRequestStatusService
                        .findCourseRegistrationRequestStatusByStatus(CourseRegistrationRequestStatusType.APPROVED)
                        .getStatusId()
        );
        keeperResponse.setStatusId(
                courseRegistrationRequestKeeperStatusService
                        .findCourseRegistrationRequestKeeperStatusByStatus(CourseRegistrationRequestKeeperStatusType.APPROVED)
                        .getStatusId()
        );
        return mapper.map(
                keeperResponse,
                CourseRegistrationRequestKeeperDto.class
        );
    }

    @Transactional
    public Long rejectRequest(Long requestId, CreateKeeperRejectionDto rejection) {
        CourseRegistrationRequest request = courseRegistrationRequestRepository
                .findById(requestId).orElseThrow(() -> new RequestNotFoundException(requestId));

        KeepersService.Keeper keeper = keeperRepository
                .findKeeperByPersonIdAndCourseId(
                        personService.getAuthenticatedPersonId(),
                        request.getCourseId()
                ).orElseThrow(KeeperNotFoundException::new);
        CourseRegistrationRequestKeeper keeperResponse = courseRegistrationRequestKeeperRepository
                .findCourseRegistrationRequestKeeperByRequestIdAndKeeperId(
                        requestId, keeper.getKeeperId()
                ).orElseThrow(DifferentKeeperException::new);

        keeperCourseRegistrationRequestValidatorService.validateRejectRequest(request, keeperResponse, rejection);

        keeperResponse.setStatusId(
                courseRegistrationRequestKeeperStatusService
                        .findCourseRegistrationRequestKeeperStatusByStatus(CourseRegistrationRequestKeeperStatusType.REJECTED)
                        .getStatusId()
        );

        return keeperRejectionRepository.save(
                new KeeperRejection(keeperResponse, rejection.getReasonId())
        ).getResponseId();
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
    public Long startTeaching(Long courseId) {
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

        approvedRequests.stream()
                .limit(authenticatedPerson.getMaxExplorers())
                .forEach(r -> {
                    r.setStatusId(acceptedStatusId);
                    explorerRepository.save(
                            new ExplorerCreateEvent(r.getPersonId(), createdGroupId)
                    );
                });
        return createdGroupId;
    }
}
