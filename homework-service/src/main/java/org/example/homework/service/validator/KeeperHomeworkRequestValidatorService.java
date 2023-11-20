package org.example.homework.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.homework.exception.classes.explorer.ExplorerGroupNotFoundException;
import org.example.homework.exception.classes.homework.HomeworkIsStillEditingException;
import org.example.homework.exception.classes.homework.HomeworkRequestAlreadyClosedException;
import org.example.homework.exception.classes.keeper.DifferentKeeperException;
import org.example.homework.exception.classes.keeper.KeeperNotFoundException;
import org.example.homework.exception.classes.request.StatusNotFoundException;
import org.example.grpc.ExplorerGroupsService;
import org.example.grpc.ExplorersService;
import org.example.grpc.KeepersService;
import org.example.homework.model.HomeworkRequest;
import org.example.homework.model.HomeworkRequestStatusType;
import org.example.homework.repository.ExplorerGroupRepository;
import org.example.homework.repository.HomeworkRequestStatusRepository;
import org.example.homework.repository.KeeperRepository;
import org.example.homework.service.PersonService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class KeeperHomeworkRequestValidatorService {
    private final HomeworkRequestStatusRepository homeworkRequestStatusRepository;
    private final ExplorerGroupRepository explorerGroupRepository;
    private final KeeperRepository keeperRepository;

    private final PersonService personService;

    @Transactional(readOnly = true)
    public void validateHomeworkRequest(ExplorersService.Explorer explorer, HomeworkRequest homeworkRequest) {
        ExplorerGroupsService.ExplorerGroup explorerGroup = explorerGroupRepository.findById(explorer.getGroupId())
                .orElseThrow(() -> new ExplorerGroupNotFoundException(explorer.getGroupId()));
        KeepersService.Keeper keeper = keeperRepository
                .findKeeperByPersonIdAndCourseId(personService.getAuthenticatedPersonId(), explorerGroup.getCourseId())
                .orElseThrow(KeeperNotFoundException::new);
        if (!(explorerGroup.getKeeperId() == keeper.getKeeperId()))
            throw new DifferentKeeperException();
        if (homeworkRequest.getStatusId().equals(getStatusId(HomeworkRequestStatusType.EDITING)))
            throw new HomeworkIsStillEditingException(homeworkRequest.getHomeworkId(), explorer.getExplorerId());
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
