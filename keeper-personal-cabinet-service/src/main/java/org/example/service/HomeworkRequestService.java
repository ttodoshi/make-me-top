package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.coursemark.MarkDTO;
import org.example.dto.homework.CreateHomeworkResponse;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.exception.classes.homeworkEX.HomeworkRequestNotFound;
import org.example.exception.classes.markEX.UnexpectedMarkValueException;
import org.example.exception.classes.requestEX.StatusNotFoundException;
import org.example.model.Explorer;
import org.example.model.homework.HomeworkMark;
import org.example.model.homework.HomeworkRequest;
import org.example.model.homework.HomeworkRequestStatusType;
import org.example.model.homework.HomeworkResponse;
import org.example.repository.*;
import org.example.repository.courseprogress.HomeworkMarkRepository;
import org.example.repository.homework.HomeworkRequestRepository;
import org.example.repository.homework.HomeworkRequestStatusRepository;
import org.example.repository.homework.HomeworkResponseRepository;
import org.example.service.validator.HomeworkRequestValidatorService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HomeworkRequestService {
    private final HomeworkRequestRepository homeworkRequestRepository;
    private final HomeworkRequestStatusRepository homeworkRequestStatusRepository;
    private final HomeworkMarkRepository homeworkMarkRepository;
    private final HomeworkResponseRepository homeworkResponseRepository;
    private final ExplorerRepository explorerRepository;

    private final HomeworkRequestValidatorService homeworkRequestValidatorService;

    public HomeworkMark setHomeworkMark(Integer homeworkId, MarkDTO mark) {
        HomeworkRequest homeworkRequest = changeRequestStatus(homeworkId, mark.getExplorerId(), HomeworkRequestStatusType.CLOSED);
        if (mark.getValue() < 1 || mark.getValue() > 5)
            throw new UnexpectedMarkValueException();
        return homeworkMarkRepository.save(
                new HomeworkMark(homeworkRequest.getRequestId(), mark.getValue())
        );
    }

    public HomeworkResponse sendHomeworkResponse(Integer homeworkId, CreateHomeworkResponse model) {
        HomeworkRequest homeworkRequest = changeRequestStatus(homeworkId, model.getExplorerId(), HomeworkRequestStatusType.EDITING);
        Optional<HomeworkResponse> homeworkResponseOptional = homeworkResponseRepository
                .findHomeworkResponseByRequestId(homeworkRequest.getRequestId());
        if (homeworkResponseOptional.isPresent()) {
            HomeworkResponse homeworkResponse = homeworkResponseOptional.get();
            homeworkResponse.setContent(model.getContent());
            return homeworkResponseRepository.save(homeworkResponse);
        }
        return homeworkResponseRepository.save(
                new HomeworkResponse(homeworkRequest.getRequestId(), model.getContent())
        );
    }

    private HomeworkRequest changeRequestStatus(Integer homeworkId, Integer explorerId, HomeworkRequestStatusType status) {
        Explorer explorer = explorerRepository.findById(explorerId)
                .orElseThrow(() -> new ExplorerNotFoundException(explorerId));
        HomeworkRequest homeworkRequest = homeworkRequestRepository
                .findHomeworkRequestByHomeworkIdAndExplorerId(homeworkId, explorerId)
                .orElseThrow(() -> new HomeworkRequestNotFound(homeworkId, explorerId));
        homeworkRequestValidatorService.validateHomeworkRequest(explorer, homeworkRequest);
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
