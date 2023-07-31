package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.courseprogress.CourseThemeCompletionDTO;
import org.example.dto.courseprogress.CourseWithThemesProgress;
import org.example.dto.homework.CreateHomeworkRequest;
import org.example.dto.homework.GetHomeworkRequest;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.exception.classes.homeworkEX.HomeworkAlreadyCheckingException;
import org.example.exception.classes.homeworkEX.HomeworkRequestAlreadyClosedException;
import org.example.exception.classes.progressEX.UnexpectedCourseThemeException;
import org.example.exception.classes.requestEX.StatusNotFoundException;
import org.example.model.Explorer;
import org.example.model.Person;
import org.example.model.course.Course;
import org.example.model.course.CourseTheme;
import org.example.model.homework.HomeworkRequest;
import org.example.model.homework.HomeworkRequestStatusType;
import org.example.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeworkRequestService {
    private final HomeworkRequestRepository homeworkRequestRepository;
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;
    private final CourseRepository courseRepository;
    private final CourseThemeRepository courseThemeRepository;
    private final HomeworkRequestStatusRepository homeworkRequestStatusRepository;
    private final CourseThemeCompletionRepository courseThemeCompletionRepository;
    private final HomeworkResponseRepository homeworkResponseRepository;

    private final ModelMapper mapper;

    @Transactional
    public HomeworkRequest sendHomeworkRequest(Integer homeworkId, CreateHomeworkRequest request) {
        final Integer authenticatedPersonId = getAuthenticatedPersonId();
        final Integer themeId = courseThemeRepository.getCourseThemeIdByHomeworkId(homeworkId);
        final Integer courseId = courseRepository.getCourseIdByThemeId(themeId);
        final Explorer explorer = getExplorer(authenticatedPersonId, courseId);
        final Integer checkingStatusId = getStatusId(HomeworkRequestStatusType.CHECKING);
        Optional<HomeworkRequest> homeworkRequestOptional = homeworkRequestRepository
                .findHomeworkRequestByHomeworkIdAndExplorerId(homeworkId, explorer.getExplorerId());
        if (homeworkRequestOptional.isPresent()) {
            HomeworkRequest homeworkRequest = homeworkRequestOptional.get();
            if (homeworkRequest.getStatusId().equals(getStatusId(HomeworkRequestStatusType.CHECKING)))
                throw new HomeworkAlreadyCheckingException(homeworkRequest.getHomeworkId());
            if (homeworkRequest.getStatusId().equals(getStatusId(HomeworkRequestStatusType.CLOSED)))
                throw new HomeworkRequestAlreadyClosedException(homeworkRequest.getRequestId());
            homeworkRequest.setContent(request.getContent());
            homeworkRequest.setStatusId(checkingStatusId);
            return homeworkRequestRepository.save(homeworkRequest);
        } else {
            Integer currentThemeId = getCurrentCourseThemeId(explorer);
            if (!currentThemeId.equals(themeId))
                throw new UnexpectedCourseThemeException(themeId, currentThemeId);
            return homeworkRequestRepository.save(
                    HomeworkRequest.builder()
                            .homeworkId(homeworkId)
                            .content(request.getContent())
                            .keeperId(getKeeperId(authenticatedPersonId, courseId))
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

    private Integer getKeeperId(Integer personId, Integer courseId) {
        return keeperRepository.getKeeperForPersonOnCourse(personId, courseId).getKeeperId();
    }

    private Integer getStatusId(HomeworkRequestStatusType status) {
        return homeworkRequestStatusRepository
                .findHomeworkRequestStatusByStatus(status)
                .orElseThrow(() -> new StatusNotFoundException(status))
                .getStatusId();
    }

    private Integer getCurrentCourseThemeId(Explorer explorer) {
        List<CourseThemeCompletionDTO> themesProgress = getThemesProgress(explorer).getThemesWithProgress();
        for (CourseThemeCompletionDTO theme : themesProgress) {
            if (!theme.getCompleted())
                return theme.getCourseThemeId();
        }
        return themesProgress.get(themesProgress.size() - 1).getCourseThemeId();
    }

    private CourseWithThemesProgress getThemesProgress(Explorer explorer) {
        Course course = courseRepository.findById(explorer.getCourseId()).orElseThrow(() -> new CourseNotFoundException(explorer.getCourseId()));
        List<CourseThemeCompletionDTO> themesCompletion = new LinkedList<>();
        for (CourseTheme ct : courseThemeRepository.findCourseThemesByCourseIdOrderByCourseThemeNumberAsc(explorer.getCourseId())) {
            Boolean themeCompleted = courseThemeCompletionRepository.findCourseThemeProgressByExplorerIdAndCourseThemeId(explorer.getExplorerId(), ct.getCourseThemeId()).isPresent();
            themesCompletion.add(
                    new CourseThemeCompletionDTO(ct.getCourseThemeId(), ct.getTitle(), themeCompleted)
            );
        }
        return CourseWithThemesProgress.builder()
                .courseId(explorer.getCourseId())
                .title(course.getTitle())
                .themesWithProgress(themesCompletion)
                .build();
    }

    @Transactional
    public List<GetHomeworkRequest> getHomeworkRequests(Integer themeId) {
        if (!courseThemeRepository.existsById(themeId))
            throw new CourseThemeNotFoundException(themeId);
        return homeworkRequestRepository.findOpenedHomeworkRequestsByThemeId(themeId).stream()
                .map(hr -> {
                    GetHomeworkRequest homeworkRequest = mapper.map(hr, GetHomeworkRequest.class);
                    homeworkRequest.setStatus(
                            homeworkRequestStatusRepository
                                    .getReferenceById(hr.getStatusId()).getStatus());
                    homeworkRequest.setResponses(
                            homeworkResponseRepository.findHomeworkResponsesByRequestId(hr.getRequestId())
                    );
                    return homeworkRequest;
                })
                .collect(Collectors.toList());
    }
}
