package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.exception.classes.keeperEX.DifferentKeeperException;
import org.example.exception.classes.requestEX.KeeperRejectionAlreadyExistsException;
import org.example.exception.classes.requestEX.RequestAlreadyClosedException;
import org.example.exception.classes.requestEX.RequestNotDeniedException;
import org.example.exception.classes.requestEX.RequestNotFoundException;
import org.example.model.Keeper;
import org.example.model.Person;
import org.example.model.courserequest.CourseRegistrationRequest;
import org.example.model.courserequest.CourseRegistrationRequestStatus;
import org.example.model.courserequest.CourseRegistrationRequestStatusType;
import org.example.repository.courseregistration.CourseRegistrationRequestRepository;
import org.example.repository.courseregistration.CourseRegistrationRequestStatusRepository;
import org.example.repository.courseregistration.KeeperRejectionRepository;
import org.example.repository.KeeperRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CourseRegistrationRequestValidatorService {
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final CourseRegistrationRequestStatusRepository courseRegistrationRequestStatusRepository;
    private final KeeperRejectionRepository keeperRejectionRepository;
    private final KeeperRepository keeperRepository;

    public void validateRequest(CourseRegistrationRequest request) {
        final Integer authenticatedPersonId = getAuthenticatedPersonId();
        CourseRegistrationRequestStatus currentStatus = courseRegistrationRequestStatusRepository.getReferenceById(request.getStatusId());
        if (!currentStatus.getStatus().equals(CourseRegistrationRequestStatusType.PROCESSING))
            throw new RequestAlreadyClosedException(request.getRequestId());
        if (authenticatedKeeperIsNotKeeperInRequest(authenticatedPersonId, request))
            throw new DifferentKeeperException();
    }

    public void validateRejectionRequest(Integer requestId) {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CourseRegistrationRequest request = courseRegistrationRequestRepository
                .findById(requestId).orElseThrow(() -> new RequestNotFoundException(requestId));
        CourseRegistrationRequestStatus currentStatus = courseRegistrationRequestStatusRepository.getReferenceById(request.getStatusId());
        if (!currentStatus.getStatus().equals(CourseRegistrationRequestStatusType.DENIED))
            throw new RequestNotDeniedException(requestId);
        if (keeperRejectionRepository.findKeeperRejectionByRequestId(requestId).isPresent())
            throw new KeeperRejectionAlreadyExistsException();
        if (authenticatedKeeperIsNotKeeperInRequest(authenticatedPerson.getPersonId(), request))
            throw new DifferentKeeperException();
    }

    private Integer getAuthenticatedPersonId() {
        final Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }

    private boolean authenticatedKeeperIsNotKeeperInRequest(Integer personId, CourseRegistrationRequest request) {
        Optional<Keeper> keeperOptional = keeperRepository.findKeeperByPersonIdAndCourseId(personId, request.getCourseId());
        return (keeperOptional.isEmpty() || !keeperOptional.get().getKeeperId().equals(request.getKeeperId()));
    }
}
