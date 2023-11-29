package org.example.homework.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.grpc.ExplorerGroupsService;
import org.example.grpc.ExplorersService;
import org.example.grpc.KeepersService;
import org.example.homework.exception.classes.explorer.ExplorerGroupNotFoundException;
import org.example.homework.exception.classes.explorer.ExplorerNotFoundException;
import org.example.homework.exception.classes.homework.HomeworkRequestAlreadyClosedException;
import org.example.homework.exception.classes.keeper.DifferentKeeperException;
import org.example.homework.exception.classes.keeper.KeeperNotFoundException;
import org.example.homework.model.HomeworkRequest;
import org.example.homework.model.HomeworkRequestStatusType;
import org.example.homework.repository.ExplorerGroupRepository;
import org.example.homework.repository.ExplorerRepository;
import org.example.homework.repository.KeeperRepository;
import org.example.homework.service.HomeworkRequestStatusService;
import org.example.homework.service.PersonService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class KeeperHomeworkRequestValidatorService {
    private final ExplorerRepository explorerRepository;
    private final ExplorerGroupRepository explorerGroupRepository;
    private final KeeperRepository keeperRepository;

    private final PersonService personService;
    private final HomeworkRequestStatusService homeworkRequestStatusService;

    @Transactional(readOnly = true)
    public void validateChangeRequestStatus(HomeworkRequest homeworkRequest) {
        ExplorersService.Explorer explorer = explorerRepository.findById(
                homeworkRequest.getExplorerId()
        ).orElseThrow(ExplorerNotFoundException::new);
        ExplorerGroupsService.ExplorerGroup explorerGroup = explorerGroupRepository.findById(explorer.getGroupId())
                .orElseThrow(() -> new ExplorerGroupNotFoundException(explorer.getGroupId()));
        KeepersService.Keeper keeper = keeperRepository
                .findKeeperByPersonIdAndCourseId(personService.getAuthenticatedPersonId(), explorerGroup.getCourseId())
                .orElseThrow(KeeperNotFoundException::new);
        if (!(explorerGroup.getKeeperId() == keeper.getKeeperId()))
            throw new DifferentKeeperException();
        Long closedStatusId = homeworkRequestStatusService.findHomeworkRequestStatusByStatus(HomeworkRequestStatusType.CLOSED).getStatusId();
        if (homeworkRequest.getStatusId().equals(closedStatusId))
            throw new HomeworkRequestAlreadyClosedException(homeworkRequest.getRequestId());
    }
}
