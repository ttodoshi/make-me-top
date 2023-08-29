package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.exception.classes.requestEX.KeeperRejectionAlreadyExistsException;
import org.example.exception.classes.requestEX.RequestNotRejectedException;
import org.example.model.courserequest.CourseRegistrationRequestKeeper;
import org.example.model.courserequest.CourseRegistrationRequestKeeperStatus;
import org.example.model.courserequest.CourseRegistrationRequestKeeperStatusType;
import org.example.repository.courserequest.CourseRegistrationRequestKeeperStatusRepository;
import org.example.repository.courserequest.KeeperRejectionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeeperRejectionValidatorService {
    private final KeeperRejectionRepository keeperRejectionRepository;
    private final CourseRegistrationRequestKeeperStatusRepository courseRegistrationRequestKeeperStatusRepository;

    public void validateRejectionRequest(CourseRegistrationRequestKeeper keeperResponse) {
        CourseRegistrationRequestKeeperStatus currentStatus = courseRegistrationRequestKeeperStatusRepository
                .getReferenceById(keeperResponse.getStatusId());
        if (!currentStatus.getStatus().equals(CourseRegistrationRequestKeeperStatusType.REJECTED))
            throw new RequestNotRejectedException(keeperResponse.getRequestId());
        if (keeperRejectionRepository.findKeeperRejectionByResponseId(keeperResponse.getResponseId()).isPresent())
            throw new KeeperRejectionAlreadyExistsException();
    }
}
