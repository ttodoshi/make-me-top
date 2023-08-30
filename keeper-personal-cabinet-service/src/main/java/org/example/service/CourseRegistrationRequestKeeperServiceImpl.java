package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.exception.classes.keeperEX.DifferentKeeperException;
import org.example.exception.classes.keeperEX.KeeperNotFoundException;
import org.example.exception.classes.requestEX.StatusNotFoundException;
import org.example.model.Keeper;
import org.example.model.Person;
import org.example.model.courserequest.CourseRegistrationRequest;
import org.example.model.courserequest.CourseRegistrationRequestKeeper;
import org.example.model.courserequest.CourseRegistrationRequestKeeperStatusType;
import org.example.repository.KeeperRepository;
import org.example.repository.courserequest.CourseRegistrationRequestKeeperRepository;
import org.example.repository.courserequest.CourseRegistrationRequestKeeperStatusRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseRegistrationRequestKeeperServiceImpl implements CourseRegistrationRequestKeeperService {
    private final CourseRegistrationRequestKeeperRepository courseRegistrationRequestKeeperRepository;
    private final CourseRegistrationRequestKeeperStatusRepository courseRegistrationRequestKeeperStatusRepository;
    private final KeeperRepository keeperRepository;

    @Override
    @Transactional
    public CourseRegistrationRequestKeeper saveKeeperResponseWithStatus(CourseRegistrationRequestKeeper keeperResponse,
                                                                        CourseRegistrationRequestKeeperStatusType status) {
        Integer keeperResponseStatusId = findCourseRegistrationRequestKeeperStatusId(
                status);
        keeperResponse.setStatusId(keeperResponseStatusId);
        return courseRegistrationRequestKeeperRepository.save(keeperResponse);
    }

    @Override
    @Transactional
    public CourseRegistrationRequestKeeper findCourseRegistrationRequestForAuthenticatedKeeper(CourseRegistrationRequest request) {
        Keeper keeper = keeperRepository
                .findKeeperByPersonIdAndCourseId(
                        getAuthenticatedPersonId(),
                        request.getCourseId()
                ).orElseThrow(KeeperNotFoundException::new);
        return courseRegistrationRequestKeeperRepository
                .findCourseRegistrationRequestKeeperByRequestIdAndKeeperId(
                        request.getRequestId(),
                        keeper.getKeeperId()
                ).orElseThrow(DifferentKeeperException::new);
    }

    private Integer getAuthenticatedPersonId() {
        final Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
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
        List<Keeper> keepersOnCourse = keeperRepository.findKeepersByCourseId(request.getCourseId());
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
                        k -> !k.getPersonId().equals(getAuthenticatedPersonId())
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

    private Integer findCourseRegistrationRequestKeeperStatusId(CourseRegistrationRequestKeeperStatusType status) {
        return courseRegistrationRequestKeeperStatusRepository
                .findCourseRegistrationRequestKeeperStatusByStatus(status)
                .orElseThrow(() -> new StatusNotFoundException(status)).getStatusId();
    }
}
