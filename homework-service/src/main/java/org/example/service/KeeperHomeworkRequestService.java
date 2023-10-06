package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.homework.CreateHomeworkFeedbackDto;
import org.example.dto.homework.HomeworkMarkDto;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.exception.classes.homeworkEX.HomeworkRequestNotFound;
import org.example.exception.classes.markEX.UnexpectedMarkValueException;
import org.example.exception.classes.requestEX.StatusNotFoundException;
import org.example.dto.explorer.ExplorerDto;
import org.example.model.*;
import org.example.repository.*;
import org.example.service.validator.KeeperHomeworkRequestValidatorService;
import org.springframework.stereotype.Service;

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

    public HomeworkMark setHomeworkMark(Integer homeworkId, HomeworkMarkDto mark) {
        HomeworkRequest homeworkRequest = changeRequestStatus(homeworkId, mark.getExplorerId(), HomeworkRequestStatusType.CLOSED);
        if (mark.getValue() < 1 || mark.getValue() > 5)
            throw new UnexpectedMarkValueException();
        return homeworkMarkRepository.save(
                new HomeworkMark(homeworkRequest.getRequestId(), mark.getValue(), mark.getComment())
        );
    }

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
        ExplorerDto explorer = explorerRepository.findById(explorerId)
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

    public HomeworkRequest getHomeworkRequest(Integer requestId) {
        return homeworkRequestRepository.findById(requestId)
                .orElseThrow(() -> new HomeworkRequestNotFound(requestId));
    }
}
