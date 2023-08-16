package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.courseregistration.CreateCourseRegistrationRequest;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.keeperEX.KeeperNotFoundException;
import org.example.exception.classes.progressEX.SystemParentsNotCompletedException;
import org.example.exception.classes.requestEX.PersonIsKeeperException;
import org.example.exception.classes.requestEX.PersonIsStudyingException;
import org.example.exception.classes.requestEX.RequestAlreadySentException;
import org.example.model.Keeper;
import org.example.repository.courseregistration.CourseRegistrationRequestRepository;
import org.example.repository.course.CourseRepository;
import org.example.repository.courseprogress.CourseThemeCompletionRepository;
import org.example.repository.KeeperRepository;
import org.example.service.CourseProgressService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseRegistrationRequestValidatorService {
    private final CourseRepository courseRepository;
    private final KeeperRepository keeperRepository;
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final CourseThemeCompletionRepository courseThemeCompletionRepository;

    private final CourseProgressService courseProgressService;

    public void validateSendRequest(Integer personId, CreateCourseRegistrationRequest request) {
        if (!courseRepository.existsById(request.getCourseId()))
            throw new CourseNotFoundException(request.getCourseId());
        if (!keeperExistsOnCourse(request.getKeeperId(), request.getCourseId()))
            throw new KeeperNotFoundException(request.getKeeperId());
        if (isCurrentlyStudying(personId))
            throw new PersonIsStudyingException();
        if (isPersonKeeperOnCourse(personId, request.getCourseId()))
            throw new PersonIsKeeperException();
        if (courseProgressService.hasUncompletedParents(personId, request.getCourseId()))
            throw new SystemParentsNotCompletedException(request.getCourseId());
        if (courseRegistrationRequestRepository.findProcessingRequest(personId).isPresent())
            throw new RequestAlreadySentException();
    }

    private boolean keeperExistsOnCourse(Integer keeperId, Integer courseId) {
        Keeper keeper = keeperRepository.findById(keeperId).orElseThrow(() -> new KeeperNotFoundException(keeperId));
        return keeper.getCourseId().equals(courseId);
    }

    private boolean isCurrentlyStudying(Integer authenticatedPersonId) {
        return courseThemeCompletionRepository.getCurrentInvestigatedCourseId(authenticatedPersonId) != null;
    }

    private boolean isPersonKeeperOnCourse(Integer authenticatedPersonId, Integer courseId) {
        return keeperRepository.findKeeperByPersonIdAndCourseId(authenticatedPersonId, courseId).isPresent();
    }
}