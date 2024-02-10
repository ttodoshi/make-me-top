package org.example.courseregistration.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.dto.courserequest.CreateKeeperRejectionDto;
import org.example.courseregistration.exception.courserequest.RejectionReasonNotFoundException;
import org.example.courseregistration.exception.courserequest.RequestAlreadyClosedException;
import org.example.courseregistration.exception.keeper.DifferentKeeperException;
import org.example.courseregistration.exception.progress.TeachingInProcessException;
import org.example.courseregistration.model.CourseRegistrationRequest;
import org.example.courseregistration.model.CourseRegistrationRequestKeeper;
import org.example.courseregistration.model.CourseRegistrationRequestKeeperStatusType;
import org.example.courseregistration.model.CourseRegistrationRequestStatusType;
import org.example.courseregistration.repository.RejectionReasonRepository;
import org.example.courseregistration.service.CourseProgressService;
import org.example.courseregistration.service.ExplorerGroupService;
import org.example.courseregistration.service.KeeperService;
import org.example.grpc.ExplorersService;
import org.example.grpc.KeepersService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeeperCourseRegistrationRequestValidatorService {
    private final RejectionReasonRepository rejectionReasonRepository;

    private final CourseProgressService courseProgressService;
    private final KeeperService keeperService;
    private final ExplorerGroupService explorerGroupService;

    @Transactional(readOnly = true)
    public void validateApproveRequest(CourseRegistrationRequest request) {
        if (!request.getStatus().getStatus().equals(CourseRegistrationRequestStatusType.PROCESSING)) {
            throw new RequestAlreadyClosedException(request.getRequestId());
        }
    }

    @Transactional(readOnly = true)
    public void validateRejectRequest(CourseRegistrationRequest request, CourseRegistrationRequestKeeper keeperResponse, CreateKeeperRejectionDto rejection) {
        if (!request.getStatus().getStatus().equals(CourseRegistrationRequestStatusType.PROCESSING))
            throw new RequestAlreadyClosedException(request.getRequestId());
        if (!keeperResponse.getStatus().getStatus().equals(CourseRegistrationRequestKeeperStatusType.PROCESSING))
            throw new RequestAlreadyClosedException(request.getRequestId());
        if (!rejectionReasonRepository.existsById(rejection.getReasonId()))
            throw new RejectionReasonNotFoundException();
    }

    @Transactional(readOnly = true)
    public void validateGetApprovedRequests(Long personId, Map<Long, KeepersService.Keeper> keepers) {
        keepers.values().forEach(k -> {
            if (!(k.getPersonId() == personId))
                throw new DifferentKeeperException();
        });
    }

    @Transactional(readOnly = true)
    public void validateStartTeachingRequest(String authorizationHeader, Long personId) {
        List<KeepersService.Keeper> personKeepers = keeperService.findKeepersByPersonId(authorizationHeader, personId);
        List<ExplorersService.Explorer> keeperExplorers = explorerGroupService
                .findExplorerGroupsByKeeperIdIn(
                        authorizationHeader,
                        personKeepers.stream().map(KeepersService.Keeper::getKeeperId).collect(Collectors.toList())
                ).stream()
                .flatMap(g -> g.getExplorersList().stream())
                .collect(Collectors.toList());
        Set<Long> explorersWithFinalAssessment = courseProgressService.getExplorersWithFinalAssessment(
                authorizationHeader,
                keeperExplorers.stream().map(ExplorersService.Explorer::getExplorerId).collect(Collectors.toList())
        );

        if (keeperExplorers.size() > explorersWithFinalAssessment.size())
            throw new TeachingInProcessException();
    }
}