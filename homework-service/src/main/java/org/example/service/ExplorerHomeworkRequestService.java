package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.explorer.ExplorerDto;
import org.example.dto.homework.CreateHomeworkRequestDto;
import org.example.dto.homework.GetHomeworkRequestDto;
import org.example.dto.person.PersonDto;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.exception.classes.homeworkEX.HomeworkNotFoundException;
import org.example.exception.classes.requestEX.StatusNotFoundException;
import org.example.model.HomeworkFeedbackStatusType;
import org.example.model.HomeworkRequest;
import org.example.model.HomeworkRequestStatusType;
import org.example.repository.*;
import org.example.service.validator.ExplorerHomeworkRequestValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final CourseThemeRepository courseThemeRepository;
    private final HomeworkRequestStatusRepository homeworkRequestStatusRepository;
    private final HomeworkFeedbackRepository homeworkFeedbackRepository;
    private final HomeworkFeedbackStatusRepository homeworkFeedbackStatusRepository;

    private final ExplorerHomeworkRequestValidatorService explorerHomeworkRequestValidatorService;

    private final ModelMapper mapper;

    @Transactional
    public HomeworkRequest sendHomeworkRequest(Integer homeworkId, CreateHomeworkRequestDto request) {
        Integer authenticatedPersonId = getAuthenticatedPersonId();
        Integer themeId = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new HomeworkNotFoundException(homeworkId))
                .getCourseThemeId();
        Integer courseId = courseThemeRepository.findById(themeId)
                .orElseThrow(() -> new CourseThemeNotFoundException(themeId))
                .getCourseId();
        ExplorerDto explorer = getExplorer(authenticatedPersonId, courseId);
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

    private Integer getAuthenticatedPersonId() {
        final PersonDto authenticatedPerson = (PersonDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }

    private ExplorerDto getExplorer(Integer personId, Integer courseId) {
        return explorerRepository.findExplorerByPersonIdAndGroup_CourseId(personId, courseId)
                .orElseThrow(() -> new ExplorerNotFoundException(courseId));
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
        Integer personId = getAuthenticatedPersonId();
        Integer courseId = courseThemeRepository.findById(themeId)
                .orElseThrow(() -> new CourseThemeNotFoundException(themeId))
                .getCourseId();
        ExplorerDto explorer = explorerRepository.findExplorerByPersonIdAndGroup_CourseId(personId, courseId)
                .orElseThrow(() -> new ExplorerNotFoundException(courseId));
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
