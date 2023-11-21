package org.example.homework.service;

import lombok.RequiredArgsConstructor;
import org.example.grpc.ExplorersService;
import org.example.homework.dto.homework.CreateHomeworkFeedbackDto;
import org.example.homework.dto.homework.CreateHomeworkMarkDto;
import org.example.homework.exception.classes.explorer.ExplorerNotFoundException;
import org.example.homework.exception.classes.homework.HomeworkRequestNotFound;
import org.example.homework.exception.classes.mark.UnexpectedMarkValueException;
import org.example.homework.exception.classes.request.StatusNotFoundException;
import org.example.homework.model.*;
import org.example.homework.repository.*;
import org.example.homework.service.validator.KeeperHomeworkRequestValidatorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KeeperHomeworkRequestService {
    private final HomeworkRequestRepository homeworkRequestRepository;
    private final HomeworkRequestStatusRepository homeworkRequestStatusRepository;
    private final HomeworkMarkRepository homeworkMarkRepository;
    private final HomeworkFeedbackRepository homeworkFeedbackRepository;
    private final HomeworkFeedbackStatusRepository homeworkFeedbackStatusRepository;
    private final ExplorerRepository explorerRepository;

    private final KeeperHomeworkRequestValidatorService keeperHomeworkRequestValidatorService;

    @Transactional
    public HomeworkMark setHomeworkMark(Integer homeworkId, CreateHomeworkMarkDto mark) {
        HomeworkRequest homeworkRequest = changeRequestStatus(homeworkId, mark.getExplorerId(), HomeworkRequestStatusType.CLOSED);
        if (mark.getValue() < 1 || mark.getValue() > 5)
            throw new UnexpectedMarkValueException();
        return homeworkMarkRepository.save(
                new HomeworkMark(homeworkRequest.getRequestId(), mark.getValue(), mark.getComment())
        );
    }

    @Transactional
    public HomeworkFeedback sendHomeworkFeedback(Integer homeworkId, CreateHomeworkFeedbackDto model) {
        HomeworkRequest homeworkRequest = changeRequestStatus(homeworkId, model.getExplorerId(), HomeworkRequestStatusType.EDITING);
        Integer openedStatusId = homeworkFeedbackStatusRepository
                .findHomeworkFeedbackStatusByStatus(HomeworkFeedbackStatusType.OPENED)
                .orElseThrow(() -> new StatusNotFoundException(HomeworkFeedbackStatusType.OPENED))
                .getStatusId();
        return homeworkFeedbackRepository.save(
                new HomeworkFeedback(homeworkRequest.getRequestId(), model.getContent(), openedStatusId)
        );
    }

    private HomeworkRequest changeRequestStatus(Integer homeworkId, Integer explorerId, HomeworkRequestStatusType status) {
        ExplorersService.Explorer explorer = explorerRepository.findById(explorerId)
                .orElseThrow(ExplorerNotFoundException::new);
        HomeworkRequest homeworkRequest = homeworkRequestRepository
                .findHomeworkRequestByHomeworkIdAndExplorerId(homeworkId, explorerId)
                .orElseThrow(() -> new HomeworkRequestNotFound(homeworkId, explorerId));
        keeperHomeworkRequestValidatorService.validateHomeworkRequest(explorer, homeworkRequest);
        homeworkRequest.setStatusId(getStatusId(status));
        return homeworkRequestRepository.save(homeworkRequest);
    }

    private Integer getStatusId(HomeworkRequestStatusType status) {
        return homeworkRequestStatusRepository
                .findHomeworkRequestStatusByStatus(status)
                .orElseThrow(() -> new StatusNotFoundException(status))
                .getStatusId();
    }

    @Transactional(readOnly = true)
    public HomeworkRequest getHomeworkRequest(Integer requestId) {
        return homeworkRequestRepository.findById(requestId)
                .orElseThrow(() -> new HomeworkRequestNotFound(requestId));
    }
}
