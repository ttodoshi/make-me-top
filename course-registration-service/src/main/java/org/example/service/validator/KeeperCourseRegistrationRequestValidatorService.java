package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.model.CourseRegistrationRequest;
import org.example.repository.CourseRegistrationRequestStatusRepository;
import org.example.repository.ExplorerRepository;
import org.example.repository.KeeperRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class KeeperCourseRegistrationRequestValidatorService {
    private final CourseRegistrationRequestStatusRepository courseRegistrationRequestStatusRepository;
    private final KeeperRepository keeperRepository;
    private final ExplorerRepository explorerRepository;

    // TODO

    @Transactional(readOnly = true)
    public void validateRequest(CourseRegistrationRequest request) {
//        CourseRegistrationRequestStatus currentStatus = courseRegistrationRequestStatusRepository.getReferenceById(request.getStatusId());
//        if (!currentStatus.getStatus().equals(CourseRegistrationRequestStatusType.PROCESSING))
//            throw new RequestAlreadyClosedException(request.getRequestId());
    }

    @Transactional(readOnly = true)
    public void validateGetApprovedRequests(Integer personId, Integer courseId) {
//        if (keeperRepository.findKeeperByPersonIdAndCourseId(personId, courseId).isEmpty())
//            throw new KeeperNotFoundException();
    }

    @Transactional(readOnly = true)
    public void validateStartTeachingRequest(Integer personId) {
//        int currentStudyingExplorersCount = explorerRepository
//                .getStudyingExplorersByKeeperPersonId(personId).size();
//        if (currentStudyingExplorersCount > 0)
//            throw new TeachingInProcessException();
    }
}
