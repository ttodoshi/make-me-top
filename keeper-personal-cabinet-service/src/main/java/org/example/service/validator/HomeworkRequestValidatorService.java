package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.exception.classes.homeworkEX.HomeworkIsStillEditingException;
import org.example.exception.classes.homeworkEX.HomeworkRequestAlreadyClosedException;
import org.example.exception.classes.keeperEX.DifferentKeeperException;
import org.example.exception.classes.keeperEX.KeeperNotFoundException;
import org.example.exception.classes.requestEX.StatusNotFoundException;
import org.example.model.Explorer;
import org.example.model.Keeper;
import org.example.model.Person;
import org.example.model.homework.HomeworkRequest;
import org.example.model.homework.HomeworkRequestStatusType;
import org.example.repository.homework.HomeworkRequestStatusRepository;
import org.example.repository.KeeperRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HomeworkRequestValidatorService {
    private final HomeworkRequestStatusRepository homeworkRequestStatusRepository;
    private final KeeperRepository keeperRepository;

    public void validateHomeworkRequest(Explorer explorer, HomeworkRequest homeworkRequest) {
        Keeper keeper = keeperRepository.findKeeperByPersonIdAndCourseId(getAuthenticatedPersonId(), explorer.getCourseId())
                .orElseThrow(KeeperNotFoundException::new);
        if (!homeworkRequest.getKeeperId().equals(keeper.getKeeperId()))
            throw new DifferentKeeperException();
        if (homeworkRequest.getStatusId().equals(getStatusId(HomeworkRequestStatusType.EDITING)))
            throw new HomeworkIsStillEditingException(homeworkRequest.getHomeworkId(), explorer.getExplorerId());
        if (homeworkRequest.getStatusId().equals(getStatusId(HomeworkRequestStatusType.CLOSED)))
            throw new HomeworkRequestAlreadyClosedException(homeworkRequest.getRequestId());
    }

    private Integer getAuthenticatedPersonId() {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }

    private Integer getStatusId(HomeworkRequestStatusType status) {
        return homeworkRequestStatusRepository
                .findHomeworkRequestStatusByStatus(status)
                .orElseThrow(() -> new StatusNotFoundException(status))
                .getStatusId();
    }
}
