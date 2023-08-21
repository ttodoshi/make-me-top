package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.courseprogress.CourseThemeCompletionDTO;
import org.example.dto.courseprogress.CourseWithThemesProgress;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.example.exception.classes.homeworkEX.HomeworkAlreadyCheckingException;
import org.example.exception.classes.homeworkEX.HomeworkRequestAlreadyClosedException;
import org.example.exception.classes.progressEX.UnexpectedCourseThemeException;
import org.example.exception.classes.requestEX.StatusNotFoundException;
import org.example.model.Explorer;
import org.example.model.course.Course;
import org.example.model.course.CourseTheme;
import org.example.model.homework.HomeworkRequest;
import org.example.model.homework.HomeworkRequestStatusType;
import org.example.repository.course.CourseRepository;
import org.example.repository.course.CourseThemeRepository;
import org.example.repository.courseprogress.CourseThemeCompletionRepository;
import org.example.repository.homework.HomeworkRequestStatusRepository;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HomeworkRequestValidatorService {
    private final CourseRepository courseRepository;
    private final CourseThemeRepository courseThemeRepository;
    private final HomeworkRequestStatusRepository homeworkRequestStatusRepository;
    private final CourseThemeCompletionRepository courseThemeCompletionRepository;

    public void validateExistingRequest(HomeworkRequest request) {
        if (request.getStatusId().equals(getStatusId(HomeworkRequestStatusType.CHECKING)))
            throw new HomeworkAlreadyCheckingException(request.getHomeworkId());
        if (request.getStatusId().equals(getStatusId(HomeworkRequestStatusType.CLOSED)))
            throw new HomeworkRequestAlreadyClosedException(request.getRequestId());
    }

    public void validateNewRequest(Integer themeId, Explorer explorer) {
        Integer currentThemeId = getCurrentCourseThemeId(explorer);
        if (!currentThemeId.equals(themeId))
            throw new UnexpectedCourseThemeException(themeId, currentThemeId);
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


    private Integer getStatusId(HomeworkRequestStatusType status) {
        return homeworkRequestStatusRepository
                .findHomeworkRequestStatusByStatus(status)
                .orElseThrow(() -> new StatusNotFoundException(status))
                .getStatusId();
    }

    public void validateGetHomeworkRequests(Integer themeId) {
        if (!courseThemeRepository.existsById(themeId))
            throw new CourseThemeNotFoundException(themeId);
    }
}
