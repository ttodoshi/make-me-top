package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.exception.classes.keeperEX.KeeperNotFoundException;
import org.example.exception.classes.progressEX.TeachingInProcessException;
import org.example.exception.classes.requestEX.KeeperRejectionAlreadyExistsException;
import org.example.exception.classes.requestEX.RequestAlreadyClosedException;
import org.example.exception.classes.requestEX.RequestNotRejectedException;
import org.example.model.courserequest.*;
import org.example.repository.ExplorerRepository;
import org.example.repository.KeeperRepository;
import org.example.repository.courserequest.*;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseRegistrationRequestValidatorService {
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final CourseRegistrationRequestStatusRepository courseRegistrationRequestStatusRepository;
    private final KeeperRejectionRepository keeperRejectionRepository;
    private final KeeperRepository keeperRepository;
    private final ExplorerRepository explorerRepository;
    private final CourseRegistrationRequestKeeperRepository courseRegistrationRequestKeeperRepository;
    private final CourseRegistrationRequestKeeperStatusRepository courseRegistrationRequestKeeperStatusRepository;

    public void validateRequest(CourseRegistrationRequest request) {
        CourseRegistrationRequestStatus currentStatus = courseRegistrationRequestStatusRepository.getReferenceById(request.getStatusId());
        if (!currentStatus.getStatus().equals(CourseRegistrationRequestStatusType.PROCESSING))
            throw new RequestAlreadyClosedException(request.getRequestId());
    }

    public void validateRejectionRequest(CourseRegistrationRequestKeeper keeperResponse) {
        CourseRegistrationRequestKeeperStatus currentStatus = courseRegistrationRequestKeeperStatusRepository
                .getReferenceById(keeperResponse.getStatusId());
        if (!currentStatus.getStatus().equals(CourseRegistrationRequestKeeperStatusType.REJECTED))
            throw new RequestNotRejectedException(keeperResponse.getRequestId());
        if (keeperRejectionRepository.findKeeperRejectionByResponseId(keeperResponse.getResponseId()).isPresent())
            throw new KeeperRejectionAlreadyExistsException();
    }

    public void validateGetApprovedRequests(Integer personId, Integer courseId) {
        if (keeperRepository.findKeeperByPersonIdAndCourseId(personId, courseId).isEmpty())
            throw new KeeperNotFoundException();
    }

    public void validateStartTeachingRequest(Integer personId, Integer courseId) {
        if (keeperRepository.findKeeperByPersonIdAndCourseId(personId, courseId).isEmpty())
            throw new KeeperNotFoundException();
        int currentStudyingExplorersCount = explorerRepository
                .getStudyingExplorersByKeeperPersonId(personId).size();
        if (currentStudyingExplorersCount > 0)
            throw new TeachingInProcessException();
    }
}
