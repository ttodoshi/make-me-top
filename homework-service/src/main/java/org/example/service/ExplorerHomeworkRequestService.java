package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.homework.CreateHomeworkRequest;
import org.example.dto.homework.GetHomeworkRequest;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.exception.classes.requestEX.StatusNotFoundException;
import org.example.model.Explorer;
import org.example.model.Person;
import org.example.model.homework.HomeworkFeedbackStatusType;
import org.example.model.homework.HomeworkRequest;
import org.example.model.homework.HomeworkRequestStatusType;
import org.example.repository.ExplorerRepository;
import org.example.repository.course.CourseRepository;
import org.example.repository.course.CourseThemeRepository;
import org.example.repository.homework.HomeworkFeedbackRepository;
import org.example.repository.homework.HomeworkFeedbackStatusRepository;
import org.example.repository.homework.HomeworkRequestRepository;
import org.example.repository.homework.HomeworkRequestStatusRepository;
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
    private final HomeworkRequestRepository homeworkRequestRepository;
    private final ExplorerRepository explorerRepository;
    private final CourseRepository courseRepository;
    private final CourseThemeRepository courseThemeRepository;
    private final HomeworkRequestStatusRepository homeworkRequestStatusRepository;
    private final HomeworkFeedbackRepository homeworkFeedbackRepository;
    private final HomeworkFeedbackStatusRepository homeworkFeedbackStatusRepository;

    private final ExplorerHomeworkRequestValidatorService explorerHomeworkRequestValidatorService;

    private final ModelMapper mapper;

    @Transactional
    public HomeworkRequest sendHomeworkRequest(Integer homeworkId, CreateHomeworkRequest request) {
        final Integer authenticatedPersonId = getAuthenticatedPersonId();
        final Integer themeId = courseThemeRepository.getCourseThemeIdByHomeworkId(homeworkId);
        final Integer courseId = courseRepository.getCourseIdByThemeId(themeId);
        final Explorer explorer = getExplorer(authenticatedPersonId, courseId);
        final Integer checkingStatusId = getCheckingStatusId();
        Optional<HomeworkRequest> homeworkRequestOptional = homeworkRequestRepository
                .findHomeworkRequestByHomeworkIdAndExplorerId(homeworkId, explorer.getExplorerId());
        if (homeworkRequestOptional.isPresent()) {
            HomeworkRequest homeworkRequest = homeworkRequestOptional.get();
            explorerHomeworkRequestValidatorService.validateExistingRequest(homeworkRequest);
            homeworkRequest.setContent(request.getContent());
            homeworkRequest.setStatusId(checkingStatusId);
            closeAllFeedbacks(homeworkRequest.getRequestId());
            return homeworkRequestRepository.save(homeworkRequest);
        } else {
            explorerHomeworkRequestValidatorService.validateNewRequest(themeId, explorer);
            return homeworkRequestRepository.save(
                    HomeworkRequest.builder()
                            .homeworkId(homeworkId)
                            .content(request.getContent())
                            .explorerId(explorer.getExplorerId())
                            .statusId(checkingStatusId)
                            .build()
            );
        }
    }

    private Integer getAuthenticatedPersonId() {
        final Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }

    private Explorer getExplorer(Integer personId, Integer courseId) {
        return explorerRepository.findExplorerByPersonIdAndCourseId(personId, courseId)
                .orElseThrow(() -> new ExplorerNotFoundException(courseId));
    }

    private Integer getCheckingStatusId() {
        return homeworkRequestStatusRepository
                .findHomeworkRequestStatusByStatus(HomeworkRequestStatusType.CHECKING)
                .orElseThrow(() -> new StatusNotFoundException(HomeworkRequestStatusType.CHECKING))
                .getStatusId();
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
    public List<GetHomeworkRequest> getHomeworkRequests(Integer themeId) {
        explorerHomeworkRequestValidatorService.validateGetHomeworkRequests(themeId);
        Integer personId = getAuthenticatedPersonId();
        Integer courseId = courseRepository.getCourseIdByThemeId(themeId);
        Explorer explorer = explorerRepository.findExplorerByPersonIdAndCourseId(personId, courseId)
                .orElseThrow(() -> new ExplorerNotFoundException(courseId));
        return homeworkRequestRepository
                .findOpenedHomeworkRequestsByThemeId(themeId, explorer.getExplorerId())
                .stream()
                .map(hr -> {
                    GetHomeworkRequest homeworkRequest = mapper.map(hr, GetHomeworkRequest.class);
                    homeworkRequest.setStatus(
                            homeworkRequestStatusRepository
                                    .getReferenceById(hr.getStatusId()).getStatus());
                    homeworkRequest.setFeedback(
                            homeworkFeedbackRepository.findOpenedHomeworkFeedbacksByRequestId(hr.getRequestId())
                    );
                    return homeworkRequest;
                })
                .collect(Collectors.toList());
    }
}
