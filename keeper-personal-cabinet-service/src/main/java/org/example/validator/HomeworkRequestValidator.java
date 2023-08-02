package org.example.validator;

import lombok.RequiredArgsConstructor;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.exception.classes.homeworkEX.HomeworkIsStillEditingException;
import org.example.exception.classes.homeworkEX.HomeworkRequestAlreadyClosedException;
import org.example.exception.classes.requestEX.StatusNotFoundException;
import org.example.model.homework.HomeworkRequest;
import org.example.model.homework.HomeworkRequestStatusType;
import org.example.repository.ExplorerRepository;
import org.example.repository.HomeworkRequestStatusRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HomeworkRequestValidator {
    private final HomeworkRequestStatusRepository homeworkRequestStatusRepository;
    private final ExplorerRepository explorerRepository;

    public void checkExplorerExists(Integer explorerId) {
        if (!explorerRepository.existsById(explorerId))
            throw new ExplorerNotFoundException();
    }

    public void validateHomeworkRequest(Integer explorerId, HomeworkRequest homeworkRequest) {
        if (homeworkRequest.getStatusId().equals(getStatusId(HomeworkRequestStatusType.EDITING)))
            throw new HomeworkIsStillEditingException(homeworkRequest.getHomeworkId(), explorerId);
        if (homeworkRequest.getStatusId().equals(getStatusId(HomeworkRequestStatusType.CLOSED)))
            throw new HomeworkRequestAlreadyClosedException(homeworkRequest.getRequestId());
    }

    private Integer getStatusId(HomeworkRequestStatusType status) {
        return homeworkRequestStatusRepository
                .findHomeworkRequestStatusByStatus(status)
                .orElseThrow(() -> new StatusNotFoundException(status))
                .getStatusId();
    }
}
