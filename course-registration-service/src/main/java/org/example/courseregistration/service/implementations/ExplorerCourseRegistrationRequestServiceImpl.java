package org.example.courseregistration.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.dto.courserequest.CreateCourseRegistrationRequestDto;
import org.example.courseregistration.dto.message.MessageDto;
import org.example.courseregistration.model.CourseRegistrationRequest;
import org.example.courseregistration.model.CourseRegistrationRequestKeeper;
import org.example.courseregistration.model.CourseRegistrationRequestKeeperStatusType;
import org.example.courseregistration.model.CourseRegistrationRequestStatusType;
import org.example.courseregistration.repository.CourseRegistrationRequestKeeperRepository;
import org.example.courseregistration.repository.CourseRegistrationRequestRepository;
import org.example.courseregistration.service.CourseRegistrationRequestKeeperStatusService;
import org.example.courseregistration.service.CourseRegistrationRequestStatusService;
import org.example.courseregistration.service.ExplorerCourseRegistrationRequestService;
import org.example.courseregistration.service.validator.ExplorerCourseRegistrationRequestValidatorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExplorerCourseRegistrationRequestServiceImpl implements ExplorerCourseRegistrationRequestService {
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final CourseRegistrationRequestKeeperRepository courseRegistrationRequestKeeperRepository;

    private final CourseRegistrationRequestKeeperStatusService courseRegistrationRequestKeeperStatusService;
    private final CourseRegistrationRequestStatusService courseRegistrationRequestStatusService;
    private final ExplorerCourseRegistrationRequestValidatorService explorerCourseRegistrationRequestValidatorService;

    @Override
    @Transactional
    public Long sendRequest(String authorizationHeader, Long authenticatedPersonId, CreateCourseRegistrationRequestDto request) {
        explorerCourseRegistrationRequestValidatorService.validateSendRequest(authorizationHeader, authenticatedPersonId, request);

        CourseRegistrationRequest sentRequest = createRequest(authenticatedPersonId, request.getCourseId());
        sendRequestToKeepers(sentRequest.getRequestId(), request.getKeeperIds());

        return sentRequest.getRequestId();
    }

    private void sendRequestToKeepers(Long requestId, List<Long> keeperIds) {
        Long keeperProcessingStatusId = courseRegistrationRequestKeeperStatusService
                .findCourseRegistrationRequestKeeperStatusByStatus(CourseRegistrationRequestKeeperStatusType.PROCESSING)
                .getStatusId();

        keeperIds.forEach(kId -> courseRegistrationRequestKeeperRepository.save(
                new CourseRegistrationRequestKeeper(
                        requestId, kId, keeperProcessingStatusId
                )
        ));
    }

    private CourseRegistrationRequest createRequest(Long personId, Long courseId) {
        return courseRegistrationRequestRepository.save(
                new CourseRegistrationRequest(
                        courseId, personId,
                        courseRegistrationRequestStatusService
                                .findCourseRegistrationRequestStatusByStatus(CourseRegistrationRequestStatusType.PROCESSING)
                                .getStatusId()
                )
        );
    }

    @Override
    @Transactional
    public MessageDto cancelRequest(Long authenticatedPersonId, Long requestId) {
        explorerCourseRegistrationRequestValidatorService.validateCancelRequest(authenticatedPersonId, requestId);

        courseRegistrationRequestRepository.deleteById(requestId);

        return new MessageDto("Вы отменили запрос на прохождение курса");
    }
}
