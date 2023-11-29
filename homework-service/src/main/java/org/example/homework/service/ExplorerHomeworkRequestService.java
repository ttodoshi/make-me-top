package org.example.homework.service;

import lombok.RequiredArgsConstructor;
import org.example.grpc.ExplorersService;
import org.example.homework.dto.homework.CreateHomeworkRequestDto;
import org.example.homework.exception.classes.explorer.ExplorerNotFoundException;
import org.example.homework.exception.classes.homework.HomeworkNotFoundException;
import org.example.homework.exception.classes.planet.PlanetNotFoundException;
import org.example.homework.model.HomeworkRequest;
import org.example.homework.model.HomeworkRequestStatusType;
import org.example.homework.model.HomeworkRequestVersion;
import org.example.homework.repository.ExplorerRepository;
import org.example.homework.repository.HomeworkRepository;
import org.example.homework.repository.HomeworkRequestRepository;
import org.example.homework.repository.PlanetRepository;
import org.example.homework.service.validator.ExplorerHomeworkRequestValidatorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExplorerHomeworkRequestService {
    private final HomeworkRepository homeworkRepository;
    private final ExplorerRepository explorerRepository;
    private final PlanetRepository planetRepository;
    private final HomeworkRequestRepository homeworkRequestRepository;

    private final HomeworkRequestService homeworkRequestService;
    private final HomeworkRequestStatusService homeworkRequestStatusService;
    private final PersonService personService;
    private final ExplorerHomeworkRequestValidatorService explorerHomeworkRequestValidatorService;

    @Transactional
    public HomeworkRequest sendHomeworkRequest(Long homeworkId, CreateHomeworkRequestDto request) {
        Long themeId = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new HomeworkNotFoundException(homeworkId))
                .getCourseThemeId();
        Long courseId = planetRepository.findById(themeId)
                .orElseThrow(() -> new PlanetNotFoundException(themeId))
                .getSystemId();
        ExplorersService.Explorer explorer = explorerRepository
                .findExplorerByPersonIdAndGroup_CourseId(
                        personService.getAuthenticatedPersonId(),
                        courseId
                ).orElseThrow(ExplorerNotFoundException::new);

        Optional<HomeworkRequest> homeworkRequestOptional = homeworkRequestRepository
                .findHomeworkRequestByHomeworkIdAndExplorerId(homeworkId, explorer.getExplorerId());

        return homeworkRequestOptional
                .map(
                        homeworkRequest -> createNewRequestVersion(
                                homeworkRequest, request.getContent())
                ).orElseGet(
                        () -> createNewRequestWithFirstVersion(
                                themeId, homeworkId, explorer.getExplorerId(), request.getContent()
                        )
                );
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

    private HomeworkRequest createNewRequestWithFirstVersion(Long themeId, Long homeworkId, Long explorerId, String content) {
        explorerHomeworkRequestValidatorService.validateNewRequest(themeId, explorerId);
        HomeworkRequest homeworkRequest = homeworkRequestService.saveHomeworkRequest(
                new HomeworkRequest(
                        homeworkId,
                        explorerId,
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
