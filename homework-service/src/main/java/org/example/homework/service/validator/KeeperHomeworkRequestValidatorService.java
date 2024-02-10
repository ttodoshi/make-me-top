package org.example.homework.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.grpc.ExplorerGroupsService;
import org.example.grpc.ExplorersService;
import org.example.grpc.KeepersService;
import org.example.homework.exception.homework.HomeworkRequestAlreadyClosedException;
import org.example.homework.exception.keeper.DifferentKeeperException;
import org.example.homework.model.HomeworkRequest;
import org.example.homework.model.HomeworkRequestStatusType;
import org.example.homework.service.ExplorerGroupService;
import org.example.homework.service.ExplorerService;
import org.example.homework.service.HomeworkRequestStatusService;
import org.example.homework.service.KeeperService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeeperHomeworkRequestValidatorService {
    private final ExplorerService explorerService;
    private final ExplorerGroupService explorerGroupService;
    private final KeeperService keeperService;
    private final HomeworkRequestStatusService homeworkRequestStatusService;

    @Transactional(readOnly = true)
    public void validateChangeRequestStatus(String authorizationHeader, Long authenticatedPersonId, HomeworkRequest homeworkRequest) {
        ExplorersService.Explorer explorer = explorerService.findById(
                authorizationHeader, homeworkRequest.getExplorerId()
        );
        ExplorerGroupsService.ExplorerGroup explorerGroup = explorerGroupService.findById(
                authorizationHeader, explorer.getGroupId()
        );
        KeepersService.Keeper keeper = keeperService.findKeeperByPersonIdAndCourseId(
                authorizationHeader, authenticatedPersonId, explorerGroup.getCourseId()
        );

        if (!(explorerGroup.getKeeperId() == keeper.getKeeperId())) {
            log.warn("authenticated person is not keeper explorer {}", explorer.getExplorerId());
            throw new DifferentKeeperException();
        }
        Long closedStatusId = homeworkRequestStatusService.findHomeworkRequestStatusByStatus(HomeworkRequestStatusType.CLOSED).getStatusId();
        if (homeworkRequest.getStatusId().equals(closedStatusId)) {
            log.warn("homework request {} already closed", homeworkRequest.getRequestId());
            throw new HomeworkRequestAlreadyClosedException(homeworkRequest.getRequestId());
        }
    }
}
