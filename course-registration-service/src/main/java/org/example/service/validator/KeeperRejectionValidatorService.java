package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.courserequest.KeeperRejectionDto;
import org.example.exception.classes.requestEX.KeeperRejectionAlreadyExistsException;
import org.example.exception.classes.requestEX.RejectionReasonNotFoundException;
import org.example.exception.classes.requestEX.RequestNotRejectedException;
import org.example.model.CourseRegistrationRequestKeeper;
import org.example.model.CourseRegistrationRequestKeeperStatus;
import org.example.model.CourseRegistrationRequestKeeperStatusType;
import org.example.repository.CourseRegistrationRequestKeeperStatusRepository;
import org.example.repository.KeeperRejectionRepository;
import org.example.repository.RejectionReasonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KeeperRejectionValidatorService {
    private final KeeperRejectionRepository keeperRejectionRepository;
    private final RejectionReasonRepository rejectionReasonRepository;
    private final CourseRegistrationRequestKeeperStatusRepository courseRegistrationRequestKeeperStatusRepository;

    @Transactional(readOnly = true)
    public void validateRejectionRequest(KeeperRejectionDto rejection, CourseRegistrationRequestKeeper keeperResponse) {
        if (!rejectionReasonRepository.existsById(rejection.getReasonId()))
            throw new RejectionReasonNotFoundException();
        CourseRegistrationRequestKeeperStatus currentStatus = courseRegistrationRequestKeeperStatusRepository
                .getReferenceById(keeperResponse.getStatusId());
        if (!currentStatus.getStatus().equals(CourseRegistrationRequestKeeperStatusType.REJECTED))
            throw new RequestNotRejectedException(keeperResponse.getRequestId());
        if (keeperRejectionRepository.findKeeperRejectionByResponseId(keeperResponse.getResponseId()).isPresent())
            throw new KeeperRejectionAlreadyExistsException();
    }
}
