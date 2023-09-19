package org.example.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.dto.keeper.KeeperDto;
import org.example.exception.classes.keeperEX.DifferentKeeperException;
import org.example.exception.classes.keeperEX.KeeperNotFoundException;
import org.example.exception.classes.requestEX.RequestNotFoundException;
import org.example.exception.classes.requestEX.StatusNotFoundException;
import org.example.model.CourseRegistrationRequest;
import org.example.model.CourseRegistrationRequestKeeper;
import org.example.model.CourseRegistrationRequestKeeperStatusType;
import org.example.repository.CourseRegistrationRequestKeeperRepository;
import org.example.repository.CourseRegistrationRequestKeeperStatusRepository;
import org.example.repository.CourseRegistrationRequestRepository;
import org.example.repository.KeeperRepository;
import org.example.service.CourseRegistrationRequestKeeperService;
import org.example.service.PersonService;
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
        KeeperDto keeper = keeperRepository
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
    public void closeRequestToOtherKeepersOnCourse(CourseRegistrationRequest request) {
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
    @Transactional
    public boolean isRequestPersonallyForKeeper(CourseRegistrationRequest request) {
        List<KeeperDto> keepersOnCourse = keeperRepository.findKeepersByCourseId(request.getCourseId());
        Integer keepersReceivedRequestCount = courseRegistrationRequestKeeperRepository
                .findCourseRegistrationRequestKeepersByRequestId(request.getRequestId()).size();
        return keepersReceivedRequestCount.equals(1) &&
                !keepersReceivedRequestCount.equals(keepersOnCourse.size());
    }

    @Override
    @Transactional
    public void openRequestToOtherKeepersOnCourse(CourseRegistrationRequest request) {
        Integer processingStatusId = findCourseRegistrationRequestKeeperStatusId(
                CourseRegistrationRequestKeeperStatusType.PROCESSING);
        keeperRepository.findKeepersByCourseId(request.getCourseId())
                .stream()
                .filter(
                        k -> !k.getPersonId().equals(personService.getAuthenticatedPersonId())
                ).forEach(k ->
                        courseRegistrationRequestKeeperRepository.save(
                                new CourseRegistrationRequestKeeper(
                                        request.getRequestId(),
                                        k.getKeeperId(),
                                        processingStatusId
                                )
                        )
                );
    }

    @Override
    public List<CourseRegistrationRequestKeeper> findCourseRegistrationRequestKeepersByRequestId(Integer requestId) {
        if (courseRegistrationRequestRepository.existsById(requestId))
            courseRegistrationRequestKeeperRepository.findCourseRegistrationRequestKeepersByRequestId(requestId);
        throw new RequestNotFoundException(requestId);
    }

    @Override
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
