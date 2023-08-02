package org.example.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.courseregistration.CreateCourseRegistrationRequest;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.keeperEX.KeeperNotFoundException;
import org.example.exception.classes.progressEX.SystemParentsNotCompletedException;
import org.example.exception.classes.requestEX.PersonIsKeeperException;
import org.example.exception.classes.requestEX.PersonIsStudyingException;
import org.example.model.Keeper;
import org.example.repository.CourseRepository;
import org.example.repository.CourseThemeCompletionRepository;
import org.example.repository.KeeperRepository;
import org.example.service.CourseProgressService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseRegistrationRequestValidator {
    private final CourseRepository courseRepository;
    private final KeeperRepository keeperRepository;
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
