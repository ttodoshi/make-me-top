package org.example.homework.service;

import lombok.RequiredArgsConstructor;
import org.example.homework.dto.homework.CreateHomeworkRequestDto;
import org.example.homework.dto.homework.GetHomeworkRequestDto;
import org.example.homework.exception.classes.explorer.ExplorerNotFoundException;
import org.example.homework.exception.classes.homework.HomeworkNotFoundException;
import org.example.homework.exception.classes.planet.PlanetNotFoundException;
import org.example.homework.exception.classes.request.StatusNotFoundException;
import org.example.grpc.ExplorersService;
import org.example.homework.model.HomeworkFeedbackStatusType;
import org.example.homework.model.HomeworkRequest;
import org.example.homework.model.HomeworkRequestStatusType;
import org.example.homework.repository.*;
import org.example.homework.service.validator.ExplorerHomeworkRequestValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExplorerHomeworkRequestService {
    private final HomeworkRepository homeworkRepository;
    private final HomeworkRequestRepository homeworkRequestRepository;
    private final ExplorerRepository explorerRepository;
    private final PlanetRepository planetRepository;
    private final HomeworkRequestStatusRepository homeworkRequestStatusRepository;
    private final HomeworkFeedbackRepository homeworkFeedbackRepository;
    private final HomeworkFeedbackStatusRepository homeworkFeedbackStatusRepository;

    private final PersonService personService;
    private final ExplorerHomeworkRequestValidatorService explorerHomeworkRequestValidatorService;

    private final ModelMapper mapper;

    @Transactional
    public HomeworkRequest sendHomeworkRequest(Integer homeworkId, CreateHomeworkRequestDto request) {
        Integer authenticatedPersonId = personService.getAuthenticatedPersonId();
        Integer themeId = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new HomeworkNotFoundException(homeworkId))
                .getCourseThemeId();
        Integer courseId = planetRepository.findById(themeId)
                .orElseThrow(() -> new PlanetNotFoundException(themeId))
                .getSystemId();
        ExplorersService.Explorer explorer = getExplorer(authenticatedPersonId, courseId);
        Optional<HomeworkRequest> homeworkRequestOptional = homeworkRequestRepository
                .findHomeworkRequestByHomeworkIdAndExplorerId(homeworkId, explorer.getExplorerId());
        if (homeworkRequestOptional.isPresent()) {
            return updateExistingRequest(homeworkRequestOptional.get(), request.getContent());
        } else {
            explorerHomeworkRequestValidatorService.validateNewRequest(themeId, explorer);
            return homeworkRequestRepository.save(
                    new HomeworkRequest(
                            homeworkId, request.getContent(),
                            explorer.getExplorerId(), getCheckingStatusId()
                    )
            );
        }
    }

    private ExplorersService.Explorer getExplorer(Integer personId, Integer courseId) {
        return explorerRepository.findExplorerByPersonIdAndGroup_CourseId(personId, courseId)
                .orElseThrow(() -> new ExplorerNotFoundException());
    }

    private Integer getCheckingStatusId() {
        return homeworkRequestStatusRepository
                .findHomeworkRequestStatusByStatus(HomeworkRequestStatusType.CHECKING)
                .orElseThrow(() -> new StatusNotFoundException(HomeworkRequestStatusType.CHECKING))
                .getStatusId();
    }

    private HomeworkRequest updateExistingRequest(HomeworkRequest homeworkRequest, String newContent) {
        explorerHomeworkRequestValidatorService.validateExistingRequest(homeworkRequest);
        homeworkRequest.setContent(newContent);
        homeworkRequest.setStatusId(getCheckingStatusId());
        closeAllFeedbacks(homeworkRequest.getRequestId());
        return homeworkRequestRepository.save(homeworkRequest);
    }

    private void closeAllFeedbacks(Integer requestId) {
        Integer closedStatusId = homeworkFeedbackStatusRepository
                .findHomeworkFeedbackStatusByStatus(HomeworkFeedbackStatusType.CLOSED)
                .orElseThrow(() -> new StatusNotFoundException(HomeworkFeedbackStatusType.CLOSED))
                .getStatusId();
        homeworkFeedbackRepository
                .findOpenedHomeworkFeedbacksByRequestId(requestId)
                .forEach(f -> {
                    f.setStatusId(closedStatusId);
                    homeworkFeedbackRepository.save(f);
                });
    }

    @Transactional(readOnly = true)
    public List<GetHomeworkRequestDto> getHomeworkRequests(Integer themeId) {
        explorerHomeworkRequestValidatorService.validateGetHomeworkRequests(themeId);
        Integer personId = personService.getAuthenticatedPersonId();
        Integer courseId = planetRepository.findById(themeId)
                .orElseThrow(() -> new PlanetNotFoundException(themeId))
                .getSystemId();
        ExplorersService.Explorer explorer = explorerRepository.findExplorerByPersonIdAndGroup_CourseId(personId, courseId)
                .orElseThrow(ExplorerNotFoundException::new);
        return homeworkRequestRepository
                .findOpenedHomeworkRequestsByThemeId(themeId, explorer.getExplorerId())
                .stream()
                .map(hr -> {
                    GetHomeworkRequestDto homeworkRequest = mapper.map(hr, GetHomeworkRequestDto.class);
                    homeworkRequest.setStatus(
                            homeworkRequestStatusRepository
                                    .getReferenceById(hr.getStatusId()).getStatus());
                    homeworkRequest.setFeedback(
                            homeworkFeedbackRepository.findOpenedHomeworkFeedbacksByRequestId(hr.getRequestId()));
                    return homeworkRequest;
                })
                .collect(Collectors.toList());
    }
}
