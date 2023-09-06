package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.exception.classes.keeperEX.KeeperNotFoundException;
import org.example.exception.classes.progressEX.TeachingInProcessException;
import org.example.exception.classes.requestEX.RequestAlreadyClosedException;
import org.example.model.courserequest.CourseRegistrationRequest;
import org.example.model.courserequest.CourseRegistrationRequestStatus;
import org.example.model.courserequest.CourseRegistrationRequestStatusType;
import org.example.repository.ExplorerRepository;
import org.example.repository.KeeperRepository;
import org.example.repository.courserequest.CourseRegistrationRequestStatusRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseRegistrationRequestValidatorService {
    private final CourseRegistrationRequestStatusRepository courseRegistrationRequestStatusRepository;
    private final KeeperRepository keeperRepository;
    private final ExplorerRepository explorerRepository;

    public void validateRequest(CourseRegistrationRequest request) {
        CourseRegistrationRequestStatus currentStatus = courseRegistrationRequestStatusRepository.getReferenceById(request.getStatusId());
        if (!currentStatus.getStatus().equals(CourseRegistrationRequestStatusType.PROCESSING))
            throw new RequestAlreadyClosedException(request.getRequestId());
    }

    public void validateGetApprovedRequests(Integer personId, Integer courseId) {
        if (keeperRepository.findKeeperByPersonIdAndCourseId(personId, courseId).isEmpty())
            throw new KeeperNotFoundException();
    }

    public void validateStartTeachingRequest(Integer personId) {
        int currentStudyingExplorersCount = explorerRepository
                .getStudyingExplorersByKeeperPersonId(personId).size();
        if (currentStudyingExplorersCount > 0)
            throw new TeachingInProcessException();
    }
}
