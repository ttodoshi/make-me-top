package org.example.courseregistration.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.courseregistration.dto.courserequest.CourseRegistrationRequestKeeperDto;
import org.example.courseregistration.exception.courserequest.RequestNotFoundException;
import org.example.courseregistration.exception.keeper.DifferentKeeperException;
import org.example.courseregistration.model.CourseRegistrationRequest;
import org.example.courseregistration.model.CourseRegistrationRequestKeeper;
import org.example.courseregistration.model.CourseRegistrationRequestKeeperStatusType;
import org.example.courseregistration.repository.CourseRegistrationRequestKeeperRepository;
import org.example.courseregistration.repository.CourseRegistrationRequestRepository;
import org.example.courseregistration.service.CourseRegistrationRequestKeeperService;
import org.example.courseregistration.service.CourseRegistrationRequestKeeperStatusService;
import org.example.courseregistration.service.KeeperService;
import org.example.grpc.KeepersService;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseRegistrationRequestKeeperServiceImpl implements CourseRegistrationRequestKeeperService {
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final CourseRegistrationRequestKeeperRepository courseRegistrationRequestKeeperRepository;

    private final CourseRegistrationRequestKeeperStatusService courseRegistrationRequestKeeperStatusService;
    private final KeeperService keeperService;

    private final ModelMapper mapper;

    @Override
    @Transactional
    public CourseRegistrationRequestKeeper findCourseRegistrationRequestKeeperForPerson(String authorizationHeader, Long personId, CourseRegistrationRequest request) {
        KeepersService.Keeper keeper = keeperService.findKeeperByPersonIdAndCourseId(
                authorizationHeader, personId, request.getCourseId()
        );

        return courseRegistrationRequestKeeperRepository
                .findCourseRegistrationRequestKeeperByRequestIdAndKeeperId(
                        request.getRequestId(), keeper.getKeeperId()
                ).orElseThrow(() -> {
                    log.warn("this request not for you");
                    return new DifferentKeeperException();
                });
    }

    @Override
    @Transactional
    public void closeRequestForKeepers(CourseRegistrationRequest request) {
        Long rejectedStatusId = courseRegistrationRequestKeeperStatusService
                .findCourseRegistrationRequestKeeperStatusByStatus(CourseRegistrationRequestKeeperStatusType.REJECTED)
                .getStatusId();

        courseRegistrationRequestKeeperRepository
                .findCourseRegistrationRequestKeepersByRequestId(request.getRequestId())
                .forEach(r -> r.setStatusId(rejectedStatusId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseRegistrationRequestKeeperDto> findCourseRegistrationRequestKeepersByRequestId(Long authenticatedPersonId, Long requestId) {
        CourseRegistrationRequest request = courseRegistrationRequestRepository
                .findById(requestId)
                .orElseThrow(RequestNotFoundException::new);
        if (!request.getPersonId().equals(authenticatedPersonId)) {
            log.warn("not yours request");
            throw new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера");
        }
        return request.getKeepersRequests()
                .stream()
                .map(kr -> mapper.map(kr, CourseRegistrationRequestKeeperDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseRegistrationRequestKeeperDto> findProcessingCourseRegistrationRequestKeepersByKeeperIdIn(String authorizationHeader, Long authenticatedPersonId, List<Long> keeperIds) {
        if (keeperService.findKeepersByPersonId(authorizationHeader, authenticatedPersonId)
                .stream()
                .noneMatch(k -> keeperIds.contains(k.getKeeperId()))) {
            log.warn("not yours requests");
            throw new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера");
        }
        return courseRegistrationRequestKeeperRepository
                .findProcessingCourseRegistrationRequestKeepersByKeeperIdIn(keeperIds)
                .stream()
                .map(kr -> mapper.map(kr, CourseRegistrationRequestKeeperDto.class))
                .collect(Collectors.toList());
    }
}
