package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.coursemark.MarkDTO;
import org.example.dto.homework.CreateHomeworkResponse;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.exception.classes.homeworkEX.*;
import org.example.exception.classes.requestEX.StatusNotFoundException;
import org.example.model.homework.HomeworkMark;
import org.example.model.homework.HomeworkRequest;
import org.example.model.homework.HomeworkRequestStatusType;
import org.example.model.homework.HomeworkResponse;
import org.example.repository.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HomeworkRequestService {
    private final HomeworkRequestRepository homeworkRequestRepository;
    private final HomeworkRequestStatusRepository homeworkRequestStatusRepository;
    private final HomeworkMarkRepository homeworkMarkRepository;
    private final HomeworkRepository homeworkRepository;
    private final ExplorerRepository explorerRepository;
    private final HomeworkResponseRepository homeworkResponseRepository;

    public HomeworkMark setHomeworkMark(Integer homeworkId, MarkDTO mark) {
        if (!explorerRepository.existsById(mark.getExplorerId()))
            throw new ExplorerNotFoundException();
        HomeworkRequest homeworkRequest = changeRequestStatus(homeworkId, mark.getExplorerId(), HomeworkRequestStatusType.CLOSED);
        if (homeworkMarkRepository.findHomeworkMarkByRequestId(homeworkRequest.getRequestId()).isPresent())
            throw new MarkAlreadyExists();
        return homeworkMarkRepository.save(
                new HomeworkMark(homeworkRequest.getRequestId(), mark.getValue())
        );
    }

    public HomeworkResponse sendHomeworkResponse(Integer homeworkId, CreateHomeworkResponse model) {
        if (!explorerRepository.existsById(model.getExplorerId()))
            throw new ExplorerNotFoundException();
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
        HomeworkRequest homeworkRequest = homeworkRequestRepository
                .findHomeworkRequestByHomeworkIdAndExplorerId(homeworkId, explorerId)
                .orElseThrow(() -> new HomeworkRequestNotFound(homeworkId, explorerId));
        if (homeworkRequest.getStatusId().equals(getStatusId(HomeworkRequestStatusType.EDITING)))
            throw new HomeworkIsStillEditingException(homeworkRequest.getHomeworkId(), explorerId);
        if (homeworkRequest.getStatusId().equals(getStatusId(HomeworkRequestStatusType.CLOSED)))
            throw new HomeworkRequestAlreadyClosedException(homeworkRequest.getRequestId());
        homeworkRequest.setStatusId(getStatusId(status));
        return homeworkRequestRepository.save(homeworkRequest);
    }

    private Integer getStatusId(HomeworkRequestStatusType status) {
        return homeworkRequestStatusRepository
                .findHomeworkRequestStatusByStatus(status)
                .orElseThrow(() -> new StatusNotFoundException(status))
                .getStatusId();
    }
}
