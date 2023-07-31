package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.courseregistration.CreateCourseRegistrationRequest;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.keeperEX.KeeperNotFoundException;
import org.example.exception.classes.progressEX.SystemParentsNotCompletedException;
import org.example.exception.classes.requestEX.PersonIsKeeperException;
import org.example.exception.classes.requestEX.PersonIsStudyingException;
import org.example.exception.classes.requestEX.StatusNotFoundException;
import org.example.model.Keeper;
import org.example.model.Person;
import org.example.model.courserequest.CourseRegistrationRequest;
import org.example.model.courserequest.CourseRegistrationRequestStatusType;
import org.example.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseRegistrationRequestService {
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final CourseRegistrationRequestStatusRepository courseRegistrationRequestStatusRepository;
    private final CourseRepository courseRepository;
    private final KeeperRepository keeperRepository;
    private final CourseThemeCompletionRepository courseThemeCompletionRepository;

    private final CourseProgressService courseProgressService;

    private final ModelMapper mapper;

    public CourseRegistrationRequest sendRequest(CreateCourseRegistrationRequest request) {
        if (!courseRepository.existsById(request.getCourseId()))
            throw new CourseNotFoundException(request.getCourseId());
        if (!keeperExistsOnCourse(request.getKeeperId(), request.getCourseId()))
            throw new KeeperNotFoundException(request.getKeeperId());
        Integer authenticatedPersonId = getAuthenticatedPersonId();
        if (isCurrentlyStudying(authenticatedPersonId))
            throw new PersonIsStudyingException();
        if (isPersonKeeperOnCourse(authenticatedPersonId, request.getCourseId()))
            throw new PersonIsKeeperException();
        if (courseProgressService.hasUncompletedParents(authenticatedPersonId, request.getCourseId()))
            throw new SystemParentsNotCompletedException(request.getCourseId());
        CourseRegistrationRequest courseRegistrationRequest = mapper.map(request, CourseRegistrationRequest.class);
        courseRegistrationRequest.setStatusId(
                courseRegistrationRequestStatusRepository
                        .findCourseRegistrationRequestStatusByStatus(CourseRegistrationRequestStatusType.PROCESSING)
                        .orElseThrow(() -> new StatusNotFoundException(CourseRegistrationRequestStatusType.PROCESSING)).getStatusId());
        courseRegistrationRequest.setPersonId(authenticatedPersonId);
        return courseRegistrationRequestRepository.save(courseRegistrationRequest);
    }

    private boolean keeperExistsOnCourse(Integer keeperId, Integer courseId) {
        Keeper keeper = keeperRepository.findById(keeperId).orElseThrow(() -> new KeeperNotFoundException(keeperId));
        return keeper.getCourseId().equals(courseId);
    }

    private Integer getAuthenticatedPersonId() {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }

    private boolean isCurrentlyStudying(Integer authenticatedPersonId) {
        return courseThemeCompletionRepository.getCurrentInvestigatedCourseId(authenticatedPersonId) != null;
    }

    private boolean isPersonKeeperOnCourse(Integer authenticatedPersonId, Integer courseId) {
        return keeperRepository.findKeeperByPersonIdAndCourseId(authenticatedPersonId, courseId).isPresent();
    }
}
