package org.example.courseregistration.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.dto.courserequest.CourseRegistrationRequestKeeperDto;
import org.example.courseregistration.exception.classes.keeper.DifferentKeeperException;
import org.example.courseregistration.exception.classes.keeper.KeeperNotFoundException;
import org.example.courseregistration.exception.classes.request.RequestNotFoundException;
import org.example.courseregistration.model.CourseRegistrationRequest;
import org.example.courseregistration.model.CourseRegistrationRequestKeeper;
import org.example.courseregistration.model.CourseRegistrationRequestKeeperStatusType;
import org.example.courseregistration.repository.CourseRegistrationRequestKeeperRepository;
import org.example.courseregistration.repository.CourseRegistrationRequestRepository;
import org.example.courseregistration.repository.KeeperRepository;
import org.example.courseregistration.service.CourseRegistrationRequestKeeperService;
import org.example.courseregistration.service.CourseRegistrationRequestKeeperStatusService;
import org.example.courseregistration.service.PersonService;
import org.example.grpc.KeepersService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseRegistrationRequestKeeperServiceImpl implements CourseRegistrationRequestKeeperService {
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final CourseRegistrationRequestKeeperRepository courseRegistrationRequestKeeperRepository;
    private final CourseRegistrationRequestKeeperStatusService courseRegistrationRequestKeeperStatusService;
    private final KeeperRepository keeperRepository;

    private final PersonService personService;

    private final ModelMapper mapper;

    @Transactional
    public CourseRegistrationRequestKeeper findCourseRegistrationRequestKeeperForPerson(Long personId, CourseRegistrationRequest request) {
        KeepersService.Keeper keeper = keeperRepository
                .findKeeperByPersonIdAndCourseId(
                        personService.getAuthenticatedPersonId(),
                        request.getCourseId()
                ).orElseThrow(KeeperNotFoundException::new);

        return courseRegistrationRequestKeeperRepository
                .findCourseRegistrationRequestKeeperByRequestIdAndKeeperId(
                        request.getRequestId(),
                        keeper.getKeeperId()
                ).orElseThrow(DifferentKeeperException::new);
    }

    @Override
    @Transactional
    public void closeRequestForKeepers(CourseRegistrationRequest request) {
        Long rejectedStatusId = courseRegistrationRequestKeeperStatusService
                .findCourseRegistrationRequestKeeperStatusByStatus(CourseRegistrationRequestKeeperStatusType.REJECTED)
                .getStatusId();

        courseRegistrationRequestKeeperRepository
                .findCourseRegistrationRequestKeepersByRequestId(request.getRequestId())
                .forEach(
                        r -> r.setStatusId(rejectedStatusId)
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseRegistrationRequestKeeperDto> findCourseRegistrationRequestKeepersByRequestId(Long requestId) {
        if (!courseRegistrationRequestRepository.existsById(requestId)) {
            throw new RequestNotFoundException(requestId);
        }
        return courseRegistrationRequestKeeperRepository
                .findCourseRegistrationRequestKeepersByRequestId(requestId)
                .stream()
                .map(kr -> mapper.map(kr, CourseRegistrationRequestKeeperDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseRegistrationRequestKeeperDto> findProcessingCourseRegistrationRequestKeepersByKeeperIdIn(List<Long> keeperIds) {
        return courseRegistrationRequestKeeperRepository
                .findProcessingCourseRegistrationRequestKeepersByKeeperIdIn(keeperIds)
                .stream()
                .map(kr -> mapper.map(kr, CourseRegistrationRequestKeeperDto.class))
                .collect(Collectors.toList());
    }
}
