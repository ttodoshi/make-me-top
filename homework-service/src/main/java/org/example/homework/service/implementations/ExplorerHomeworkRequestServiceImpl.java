package org.example.homework.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.grpc.ExplorersService;
import org.example.homework.config.security.RoleService;
import org.example.homework.dto.homeworkrequest.CreateHomeworkRequestDto;
import org.example.homework.enums.CourseRoleType;
import org.example.homework.exception.homework.HomeworkNotFoundException;
import org.example.homework.model.HomeworkRequest;
import org.example.homework.model.HomeworkRequestStatusType;
import org.example.homework.model.HomeworkRequestVersion;
import org.example.homework.repository.HomeworkRepository;
import org.example.homework.repository.HomeworkRequestRepository;
import org.example.homework.service.ExplorerHomeworkRequestService;
import org.example.homework.service.ExplorerService;
import org.example.homework.service.HomeworkRequestStatusService;
import org.example.homework.service.PlanetService;
import org.example.homework.service.validator.ExplorerHomeworkRequestValidatorService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExplorerHomeworkRequestServiceImpl implements ExplorerHomeworkRequestService {
    private final HomeworkRepository homeworkRepository;
    private final HomeworkRequestRepository homeworkRequestRepository;

    private final RoleService roleService;
    private final ExplorerService explorerService;
    private final PlanetService planetService;
    private final HomeworkRequestStatusService homeworkRequestStatusService;
    private final ExplorerHomeworkRequestValidatorService explorerHomeworkRequestValidatorService;

    @Override
    @Transactional
    public Long sendHomeworkRequest(String authorizationHeader, Long authenticatedPersonId, Long homeworkId, CreateHomeworkRequestDto request) {
        if (!roleService.hasAnyCourseRoleByHomeworkId(authorizationHeader, authenticatedPersonId, homeworkId, CourseRoleType.EXPLORER)) {
            throw new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера");
        }

        Long themeId = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> {
                    log.warn("homework by id {} not found", homeworkId);
                    return new HomeworkNotFoundException(homeworkId);
                }).getCourseThemeId();
        Long courseId = planetService.findById(authorizationHeader, themeId).getSystemId();
        ExplorersService.Explorer explorer = explorerService.findExplorerByPersonIdAndGroup_CourseId(
                authorizationHeader, authenticatedPersonId, courseId
        );

        Optional<HomeworkRequest> homeworkRequestOptional = homeworkRequestRepository
                .findHomeworkRequestByHomeworkIdAndExplorerId(homeworkId, explorer.getExplorerId());

        return homeworkRequestOptional
                .map(hr -> createNewRequestVersion(hr, request.getContent()))
                .orElseGet(
                        () -> createNewRequestWithFirstVersion(
                                authorizationHeader,
                                themeId, homeworkId,
                                explorer.getExplorerId(), request.getContent()
                        )
                ).getRequestId();
    }

    private HomeworkRequest createNewRequestVersion(HomeworkRequest homeworkRequest, String newContent) {
        explorerHomeworkRequestValidatorService.validateNewRequestVersion(homeworkRequest);

        homeworkRequest.getHomeworkRequestVersions().add(
                new HomeworkRequestVersion(
                        homeworkRequest.getRequestId(),
                        newContent
                )
        );
        homeworkRequest.setStatusId(
                homeworkRequestStatusService.findHomeworkRequestStatusByStatus(
                        HomeworkRequestStatusType.CHECKING
                ).getStatusId()
        );

        return homeworkRequest;
    }

    private HomeworkRequest createNewRequestWithFirstVersion(String authorizationHeader, Long themeId, Long homeworkId, Long explorerId, String content) {
        explorerHomeworkRequestValidatorService.validateNewRequest(authorizationHeader, themeId, explorerId);

        HomeworkRequest homeworkRequest = homeworkRequestRepository.save(
                new HomeworkRequest(
                        homeworkId, explorerId,
                        homeworkRequestStatusService.findHomeworkRequestStatusByStatus(
                                HomeworkRequestStatusType.CHECKING
                        ).getStatusId()
                )
        );

        homeworkRequest.setHomeworkRequestVersions(
                Collections.singletonList(
                        new HomeworkRequestVersion(
                                homeworkRequest.getRequestId(),
                                content
                        )
                )
        );
        return homeworkRequest;
    }
}
