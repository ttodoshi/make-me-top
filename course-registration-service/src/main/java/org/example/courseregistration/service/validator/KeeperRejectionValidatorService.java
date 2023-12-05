package org.example.courseregistration.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.dto.courserequest.CreateKeeperRejectionDto;
import org.example.courseregistration.exception.classes.request.KeeperRejectionAlreadyExistsException;
import org.example.courseregistration.exception.classes.request.RejectionReasonNotFoundException;
import org.example.courseregistration.exception.classes.request.RequestNotRejectedException;
import org.example.courseregistration.model.CourseRegistrationRequestKeeper;
import org.example.courseregistration.model.CourseRegistrationRequestKeeperStatus;
import org.example.courseregistration.model.CourseRegistrationRequestKeeperStatusType;
import org.example.courseregistration.repository.CourseRegistrationRequestKeeperStatusRepository;
import org.example.courseregistration.repository.KeeperRejectionRepository;
import org.example.courseregistration.repository.RejectionReasonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KeeperRejectionValidatorService {
    private final KeeperRejectionRepository keeperRejectionRepository;
    private final RejectionReasonRepository rejectionReasonRepository;
    private final CourseRegistrationRequestKeeperStatusRepository courseRegistrationRequestKeeperStatusRepository;

    @Transactional(readOnly = true)
    public void validateRejectionRequest(CreateKeeperRejectionDto rejection, CourseRegistrationRequestKeeper keeperResponse) {
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
