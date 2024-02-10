package org.example.homework.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.homework.dto.homeworkmark.CreateHomeworkMarkDto;
import org.example.homework.dto.homeworkrequest.CreateHomeworkRequestFeedbackDto;
import org.example.homework.model.HomeworkMark;
import org.example.homework.model.HomeworkRequest;
import org.example.homework.model.HomeworkRequestFeedback;
import org.example.homework.model.HomeworkRequestStatusType;
import org.example.homework.repository.HomeworkMarkRepository;
import org.example.homework.repository.HomeworkRequestFeedbackRepository;
import org.example.homework.repository.HomeworkRequestRepository;
import org.example.homework.repository.HomeworkRequestVersionRepository;
import org.example.homework.service.HomeworkRequestService;
import org.example.homework.service.HomeworkRequestStatusService;
import org.example.homework.service.KeeperHomeworkRequestService;
import org.example.homework.service.validator.KeeperHomeworkRequestValidatorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KeeperHomeworkRequestServiceImpl implements KeeperHomeworkRequestService {
    private final HomeworkRequestRepository homeworkRequestRepository;
    private final HomeworkMarkRepository homeworkMarkRepository;
    private final HomeworkRequestFeedbackRepository homeworkRequestFeedbackRepository;
    private final HomeworkRequestVersionRepository homeworkRequestVersionRepository;

    private final HomeworkRequestService homeworkRequestService;
    private final HomeworkRequestStatusService homeworkRequestStatusService;
    private final KeeperHomeworkRequestValidatorService keeperHomeworkRequestValidatorService;

    @Override
    @Transactional
    public Long setHomeworkMark(String authorizationHeader, Long authenticatedPersonId, Long requestId, CreateHomeworkMarkDto mark) {
        HomeworkRequest homeworkRequest = changeRequestStatus(
                authorizationHeader, authenticatedPersonId,
                requestId, HomeworkRequestStatusType.CLOSED
        );
        return homeworkMarkRepository.save(
                new HomeworkMark(homeworkRequest.getRequestId(), mark.getComment())
        ).getRequestId();
    }

    @Override
    @Transactional
    public Long sendHomeworkRequestFeedback(String authorizationHeader, Long authenticatedPersonId, Long requestId, CreateHomeworkRequestFeedbackDto feedback) {
        HomeworkRequest homeworkRequest = changeRequestStatus(
                authorizationHeader, authenticatedPersonId,
                requestId, HomeworkRequestStatusType.EDITING
        );
        return homeworkRequestFeedbackRepository.save(
                new HomeworkRequestFeedback(
                        homeworkRequestVersionRepository
                                .findLastHomeworkRequestVersionByRequestId(
                                        homeworkRequest.getRequestId()
                                ).getVersionId(),
                        feedback.getContent()
                )
        ).getFeedbackId();
    }

    private HomeworkRequest changeRequestStatus(String authorizationHeader, Long authenticatedPersonId, Long requestId, HomeworkRequestStatusType status) {
        HomeworkRequest homeworkRequest = homeworkRequestService
                .findHomeworkRequestById(requestId);

        keeperHomeworkRequestValidatorService.validateChangeRequestStatus(
                authorizationHeader, authenticatedPersonId, homeworkRequest
        );

        homeworkRequest.setStatusId(
                homeworkRequestStatusService
                        .findHomeworkRequestStatusByStatus(status)
                        .getStatusId()
        );
        return homeworkRequestRepository.save(homeworkRequest);
    }
}
