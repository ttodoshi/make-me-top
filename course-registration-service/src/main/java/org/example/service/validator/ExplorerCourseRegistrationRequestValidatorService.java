package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.courserequest.CreateCourseRegistrationRequestDto;
import org.example.dto.keeper.KeeperDto;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.keeperEX.KeeperNotFoundException;
import org.example.exception.classes.progressEX.AlreadyStudyingException;
import org.example.exception.classes.progressEX.PersonIsStudyingException;
import org.example.exception.classes.progressEX.SystemParentsNotCompletedException;
import org.example.exception.classes.requestEX.PersonIsKeeperException;
import org.example.exception.classes.requestEX.PersonIsNotPersonInRequestException;
import org.example.exception.classes.requestEX.RequestAlreadySentException;
import org.example.exception.classes.requestEX.StatusNotFoundException;
import org.example.model.CourseRegistrationRequest;
import org.example.model.CourseRegistrationRequestStatusType;
import org.example.repository.*;
import org.example.service.CourseProgressService;
import org.example.service.PersonService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExplorerCourseRegistrationRequestValidatorService {
    private final CourseRepository courseRepository;
    private final KeeperRepository keeperRepository;
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final CourseRegistrationRequestStatusRepository courseRegistrationRequestStatusRepository;
    private final GalaxyRepository galaxyRepository;

    private final PersonService personService;
    private final CourseProgressService courseProgressService;

    public void validateSendRequest(Integer personId, CreateCourseRegistrationRequestDto request) {
        if (!courseRepository.existsById(request.getCourseId()))
            throw new CourseNotFoundException(request.getCourseId());
        request.getKeeperIds().forEach(kId -> {
            if (!keeperExistsOnCourse(kId, request.getCourseId()))
                throw new KeeperNotFoundException(kId);
        });
        if (courseProgressService.isAuthenticatedPersonCurrentlyStudying(
                galaxyRepository.getGalaxyBySystemId(request.getCourseId()).getGalaxyId())) {
            throw new PersonIsStudyingException();
        }
        if (isPersonKeeperOnCourse(personId, request.getCourseId()))
            throw new PersonIsKeeperException();
        if (!courseProgressService.isCourseOpenedForAuthenticatedPerson(request.getCourseId()))
            throw new SystemParentsNotCompletedException(request.getCourseId());
        if (courseRegistrationRequestRepository.findCourseRegistrationRequestByPersonIdAndStatus_ProcessingStatus(personId).isPresent())
            throw new RequestAlreadySentException();
    }

    private boolean keeperExistsOnCourse(Integer keeperId, Integer courseId) {
        KeeperDto keeper = keeperRepository.findById(keeperId)
                .orElseThrow(() -> new KeeperNotFoundException(keeperId));
        return keeper.getCourseId().equals(courseId);
    }

    private boolean isPersonKeeperOnCourse(Integer authenticatedPersonId, Integer courseId) {
        return keeperRepository.findKeeperByPersonIdAndCourseId(authenticatedPersonId, courseId).isPresent();
    }

    public void validateCancelRequest(CourseRegistrationRequest request) {
        if (!request.getPersonId().equals(personService.getAuthenticatedPersonId()))
            throw new PersonIsNotPersonInRequestException();
        Integer acceptedStatusId = courseRegistrationRequestStatusRepository
                .findCourseRegistrationRequestStatusByStatus(CourseRegistrationRequestStatusType.ACCEPTED)
                .orElseThrow(() -> new StatusNotFoundException(CourseRegistrationRequestStatusType.ACCEPTED))
                .getStatusId();
        if (request.getStatusId().equals(acceptedStatusId))
            throw new AlreadyStudyingException();
    }
}
