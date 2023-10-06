package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.model.CourseRegistrationRequestKeeper;
import org.example.repository.CourseRegistrationRequestKeeperStatusRepository;
import org.example.repository.KeeperRejectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KeeperRejectionValidatorService {
    private final KeeperRejectionRepository keeperRejectionRepository;
    private final CourseRegistrationRequestKeeperStatusRepository courseRegistrationRequestKeeperStatusRepository;

    // TODO

    @Transactional(readOnly = true)
    public void validateRejectionRequest(CourseRegistrationRequestKeeper keeperResponse) {
//        CourseRegistrationRequestKeeperStatus currentStatus = courseRegistrationRequestKeeperStatusRepository
//                .getReferenceById(keeperResponse.getStatusId());
//        if (!currentStatus.getStatus().equals(CourseRegistrationRequestKeeperStatusType.REJECTED))
//            throw new RequestNotRejectedException(keeperResponse.getRequestId());
//        if (keeperRejectionRepository.findKeeperRejectionByResponseId(keeperResponse.getResponseId()).isPresent())
//            throw new KeeperRejectionAlreadyExistsException();
    }
}
