package org.example.courseregistration.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.exception.classes.keeper.DifferentKeeperException;
import org.example.courseregistration.exception.classes.keeper.KeeperNotFoundException;
import org.example.courseregistration.exception.classes.request.RequestNotFoundException;
import org.example.courseregistration.exception.classes.request.StatusNotFoundException;
import org.example.courseregistration.repository.CourseRegistrationRequestKeeperRepository;
import org.example.courseregistration.repository.CourseRegistrationRequestKeeperStatusRepository;
import org.example.courseregistration.repository.CourseRegistrationRequestRepository;
import org.example.courseregistration.repository.KeeperRepository;
import org.example.grpc.KeepersService;
import org.example.courseregistration.model.CourseRegistrationRequest;
import org.example.courseregistration.model.CourseRegistrationRequestKeeper;
import org.example.courseregistration.model.CourseRegistrationRequestKeeperStatusType;
import org.example.courseregistration.service.CourseRegistrationRequestKeeperService;
import org.example.courseregistration.service.PersonService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseRegistrationRequestKeeperServiceImpl implements CourseRegistrationRequestKeeperService {
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final CourseRegistrationRequestKeeperRepository courseRegistrationRequestKeeperRepository;
    private final CourseRegistrationRequestKeeperStatusRepository courseRegistrationRequestKeeperStatusRepository;
    private final KeeperRepository keeperRepository;

    private final PersonService personService;

    @Override
    @Transactional
    public CourseRegistrationRequestKeeper saveKeeperResponseWithStatus(CourseRegistrationRequestKeeper keeperResponse,
                                                                        CourseRegistrationRequestKeeperStatusType status) {
        Integer keeperResponseStatusId = findCourseRegistrationRequestKeeperStatusId(status);
        keeperResponse.setStatusId(keeperResponseStatusId);
        return courseRegistrationRequestKeeperRepository.save(keeperResponse);
    }

    @Override
    @Transactional
    public CourseRegistrationRequestKeeper findCourseRegistrationRequestForAuthenticatedKeeper(CourseRegistrationRequest request) {
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
    public void closeRequestToOtherKeepers(CourseRegistrationRequest request) {
        Integer processingStatusId = findCourseRegistrationRequestKeeperStatusId(
                CourseRegistrationRequestKeeperStatusType.PROCESSING);
        Integer rejectedStatusId = findCourseRegistrationRequestKeeperStatusId(
                CourseRegistrationRequestKeeperStatusType.REJECTED);
        courseRegistrationRequestKeeperRepository
                .findCourseRegistrationRequestKeepersByRequestId(request.getRequestId())
                .stream()
                .filter(
                        r -> r.getStatusId().equals(processingStatusId)
                ).forEach(
                        r -> {
                            r.setStatusId(rejectedStatusId);
                            courseRegistrationRequestKeeperRepository.save(r);
                        }
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseRegistrationRequestKeeper> findCourseRegistrationRequestKeepersByRequestId(Integer requestId) {
        if (!courseRegistrationRequestRepository.existsById(requestId)) {
            throw new RequestNotFoundException(requestId);
        }
        return courseRegistrationRequestKeeperRepository.findCourseRegistrationRequestKeepersByRequestId(requestId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseRegistrationRequestKeeper> findProcessingCourseRegistrationRequestKeepersByKeeperIdIn(List<Integer> keeperIds) {
        return courseRegistrationRequestKeeperRepository
                .findProcessingCourseRegistrationRequestKeepersByKeeperIdIn(keeperIds);
    }

    private Integer findCourseRegistrationRequestKeeperStatusId(CourseRegistrationRequestKeeperStatusType status) {
        return courseRegistrationRequestKeeperStatusRepository
                .findCourseRegistrationRequestKeeperStatusByStatus(status)
                .orElseThrow(() -> new StatusNotFoundException(status)).getStatusId();
    }
}
