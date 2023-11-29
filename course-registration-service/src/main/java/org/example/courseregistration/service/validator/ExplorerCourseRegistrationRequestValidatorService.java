package org.example.courseregistration.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.dto.courserequest.CreateCourseRegistrationRequestDto;
import org.example.courseregistration.exception.classes.course.CourseNotFoundException;
import org.example.courseregistration.exception.classes.keeper.KeeperNotFoundException;
import org.example.courseregistration.exception.classes.progress.AlreadyStudyingException;
import org.example.courseregistration.exception.classes.progress.PersonIsStudyingException;
import org.example.courseregistration.exception.classes.progress.SystemParentsNotCompletedException;
import org.example.courseregistration.exception.classes.request.PersonIsKeeperException;
import org.example.courseregistration.exception.classes.request.PersonIsNotPersonInRequestException;
import org.example.courseregistration.exception.classes.request.RequestAlreadySentException;
import org.example.courseregistration.model.CourseRegistrationRequest;
import org.example.courseregistration.model.CourseRegistrationRequestStatusType;
import org.example.courseregistration.repository.CourseRegistrationRequestRepository;
import org.example.courseregistration.repository.CourseRepository;
import org.example.courseregistration.repository.GalaxyRepository;
import org.example.courseregistration.repository.KeeperRepository;
import org.example.courseregistration.service.CourseProgressService;
import org.example.courseregistration.service.CourseRegistrationRequestStatusService;
import org.example.courseregistration.service.PersonService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExplorerCourseRegistrationRequestValidatorService {
    private final CourseRepository courseRepository;
    private final KeeperRepository keeperRepository;
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final CourseRegistrationRequestStatusService courseRegistrationRequestStatusService;
    private final GalaxyRepository galaxyRepository;

    private final PersonService personService;
    private final CourseProgressService courseProgressService;

    public void validateSendRequest(Long personId, CreateCourseRegistrationRequestDto request) {
        if (!courseRepository.existsById(request.getCourseId()))
            throw new CourseNotFoundException(request.getCourseId());
        keeperRepository.findKeepersByKeeperIdIn(request.getKeeperIds())
                .values()
                .forEach(k -> {
                    if (!request.getCourseId().equals(k.getCourseId()))
                        throw new KeeperNotFoundException(k.getKeeperId());
                });
        if (courseProgressService.isAuthenticatedPersonCurrentlyStudying(
                galaxyRepository.findGalaxyBySystemId(request.getCourseId()).getGalaxyId())) {
            throw new PersonIsStudyingException();
        }
        if (isPersonKeeperOnCourse(personId, request.getCourseId()))
            throw new PersonIsKeeperException();
        if (!courseProgressService.isCourseOpenedForAuthenticatedPerson(request.getCourseId()))
            throw new SystemParentsNotCompletedException(request.getCourseId());
        if (courseRegistrationRequestRepository.findCourseRegistrationRequestByPersonIdAndStatus_NotAccepted(personId).isPresent())
            throw new RequestAlreadySentException();
    }

    private boolean isPersonKeeperOnCourse(Long authenticatedPersonId, Long courseId) {
        return keeperRepository.findKeeperByPersonIdAndCourseId(authenticatedPersonId, courseId).isPresent();
    }

    public void validateCancelRequest(CourseRegistrationRequest request) {
        if (!request.getPersonId().equals(personService.getAuthenticatedPersonId()))
            throw new PersonIsNotPersonInRequestException();
        Long acceptedStatusId = courseRegistrationRequestStatusService
                .findCourseRegistrationRequestStatusByStatus(CourseRegistrationRequestStatusType.ACCEPTED)
                .getStatusId();
        if (request.getStatusId().equals(acceptedStatusId))
            throw new AlreadyStudyingException();
    }
}
