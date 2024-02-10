package org.example.courseregistration.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.dto.courserequest.CreateCourseRegistrationRequestDto;
import org.example.courseregistration.exception.course.CourseNotFoundException;
import org.example.courseregistration.exception.courserequest.PersonIsKeeperException;
import org.example.courseregistration.exception.courserequest.PersonIsNotPersonInRequestException;
import org.example.courseregistration.exception.courserequest.RequestAlreadySentException;
import org.example.courseregistration.exception.courserequest.RequestNotFoundException;
import org.example.courseregistration.exception.keeper.KeeperNotFoundException;
import org.example.courseregistration.exception.progress.AlreadyStudyingException;
import org.example.courseregistration.exception.progress.PersonIsStudyingException;
import org.example.courseregistration.exception.progress.SystemParentsNotCompletedException;
import org.example.courseregistration.model.CourseRegistrationRequest;
import org.example.courseregistration.model.CourseRegistrationRequestStatusType;
import org.example.courseregistration.repository.CourseRegistrationRequestRepository;
import org.example.courseregistration.service.CourseProgressService;
import org.example.courseregistration.service.CourseRegistrationRequestStatusService;
import org.example.courseregistration.service.CourseService;
import org.example.courseregistration.service.KeeperService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExplorerCourseRegistrationRequestValidatorService {
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;

    private final CourseService courseService;
    private final KeeperService keeperService;
    private final CourseRegistrationRequestStatusService courseRegistrationRequestStatusService;

    private final CourseProgressService courseProgressService;

    public void validateSendRequest(String authorizationHeader, Long personId, CreateCourseRegistrationRequestDto request) {
        if (!courseService.existsById(authorizationHeader, request.getCourseId()))
            throw new CourseNotFoundException(request.getCourseId());
        keeperService.findKeepersByKeeperIdIn(authorizationHeader, request.getKeeperIds())
                .values()
                .forEach(k -> {
                    if (!request.getCourseId().equals(k.getCourseId()))
                        throw new KeeperNotFoundException(k.getKeeperId());
                });
        if (courseProgressService.isAuthenticatedPersonCurrentlyStudying(authorizationHeader, personId))
            throw new PersonIsStudyingException();
        if (keeperService.existsKeeperByPersonIdAndCourseId(authorizationHeader, personId, request.getCourseId()))
            throw new PersonIsKeeperException();
        if (!courseProgressService.isCourseOpenedForAuthenticatedPerson(authorizationHeader, request.getCourseId()))
            throw new SystemParentsNotCompletedException(request.getCourseId());
        if (courseRegistrationRequestRepository.findCourseRegistrationRequestByPersonIdAndStatus_NotAccepted(personId).isPresent())
            throw new RequestAlreadySentException();
    }

    public void validateCancelRequest(Long authenticatedPersonId, Long requestId) {
        CourseRegistrationRequest request = courseRegistrationRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException(requestId));

        if (!request.getPersonId().equals(authenticatedPersonId))
            throw new PersonIsNotPersonInRequestException();
        Long acceptedStatusId = courseRegistrationRequestStatusService
                .findCourseRegistrationRequestStatusByStatus(CourseRegistrationRequestStatusType.ACCEPTED)
                .getStatusId();
        if (request.getStatusId().equals(acceptedStatusId))
            throw new AlreadyStudyingException();
    }
}
