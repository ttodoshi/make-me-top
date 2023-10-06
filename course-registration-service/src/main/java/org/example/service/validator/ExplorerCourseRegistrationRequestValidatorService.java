package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.courserequest.CreateCourseRegistrationRequestDto;
import org.example.model.CourseRegistrationRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExplorerCourseRegistrationRequestValidatorService {
    // TODO
//    private final CourseRepository courseRepository;
//    private final KeeperRepository keeperRepository;
//    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
//    private final CourseThemeCompletionRepository courseThemeCompletionRepository;
//    private final CourseRegistrationRequestStatusRepository courseRegistrationRequestStatusRepository;
//
//    private final PersonService personService;
//    private final CourseProgressService courseProgressService;

    public void validateSendRequest(Integer personId, CreateCourseRegistrationRequestDto request) {
//        if (!courseRepository.existsById(request.getCourseId()))
//            throw new CourseNotFoundException(request.getCourseId());
//        if (request.getKeeperId() != null && !keeperExistsOnCourse(request.getKeeperId(), request.getCourseId()))
//            throw new KeeperNotFoundException(request.getKeeperId());
//        if (isCurrentlyStudying(personId))
//            throw new PersonIsStudyingException();
//        if (isPersonKeeperOnCourse(personId, request.getCourseId()))
//            throw new PersonIsKeeperException();
//        if (courseProgressService.hasUncompletedParents(personId, request.getCourseId()))
//            throw new SystemParentsNotCompletedException(request.getCourseId());
//        if (courseRegistrationRequestRepository.findProcessingRequest(personId).isPresent())
//            throw new RequestAlreadySentException();
    }

//    private boolean keeperExistsOnCourse(Integer keeperId, Integer courseId) {
//        KeeperDto keeper = keeperRepository.findById(keeperId).orElseThrow(() -> new KeeperNotFoundException(keeperId));
//        return keeper.getCourseId().equals(courseId);
//    }

//    private boolean isCurrentlyStudying(Integer authenticatedPersonId) {
//        return courseThemeCompletionRepository.getCurrentInvestigatedCourseId(authenticatedPersonId).isPresent();
//    }

//    private boolean isPersonKeeperOnCourse(Integer authenticatedPersonId, Integer courseId) {
//        return keeperRepository.findKeeperByPersonIdAndCourseId(authenticatedPersonId, courseId).isPresent();
//    }

    public void validateCancelRequest(CourseRegistrationRequest request) {
//        if (!request.getPersonId().equals(personService.getAuthenticatedPersonId()))
//            throw new PersonIsNotPersonInRequestException();
//        Integer acceptedStatusId = courseRegistrationRequestStatusRepository
//                .findCourseRegistrationRequestStatusByStatus(CourseRegistrationRequestStatusType.ACCEPTED)
//                .orElseThrow(() -> new StatusNotFoundException(CourseRegistrationRequestStatusType.ACCEPTED))
//                .getStatusId();
//        if (request.getStatusId().equals(acceptedStatusId))
//            throw new AlreadyStudyingException();
    }
}
