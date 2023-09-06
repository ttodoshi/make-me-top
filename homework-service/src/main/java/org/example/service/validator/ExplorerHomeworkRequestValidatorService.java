package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.courseprogress.CourseThemeCompletionDTO;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.example.exception.classes.homeworkEX.HomeworkAlreadyCheckingException;
import org.example.exception.classes.homeworkEX.HomeworkRequestAlreadyClosedException;
import org.example.exception.classes.progressEX.UnexpectedCourseThemeException;
import org.example.exception.classes.requestEX.StatusNotFoundException;
import org.example.model.Explorer;
import org.example.model.homework.HomeworkRequest;
import org.example.model.homework.HomeworkRequestStatusType;
import org.example.repository.course.CourseThemeRepository;
import org.example.repository.homework.HomeworkRequestStatusRepository;
import org.example.service.CourseThemesProgressService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ExplorerHomeworkRequestValidatorService {
    private final CourseThemeRepository courseThemeRepository;
    private final HomeworkRequestStatusRepository homeworkRequestStatusRepository;

    private final CourseThemesProgressService courseThemesProgressService;

    public void validateExistingRequest(HomeworkRequest request) {
        if (request.getStatusId().equals(getStatusId(HomeworkRequestStatusType.CHECKING)))
            throw new HomeworkAlreadyCheckingException(request.getHomeworkId());
        if (request.getStatusId().equals(getStatusId(HomeworkRequestStatusType.CLOSED)))
            throw new HomeworkRequestAlreadyClosedException(request.getRequestId());
    }

    public void validateNewRequest(Integer themeId, Explorer explorer) {
        Integer currentThemeId = getCurrentCourseThemeId(explorer);
        if (!currentThemeId.equals(themeId))
            throw new UnexpectedCourseThemeException(currentThemeId, themeId);
    }

    private Integer getCurrentCourseThemeId(Explorer explorer) {
        List<CourseThemeCompletionDTO> themesProgress = courseThemesProgressService
                .getThemesProgress(explorer).getThemesWithProgress();
        for (CourseThemeCompletionDTO theme : themesProgress) {
            if (!theme.getCompleted())
                return theme.getCourseThemeId();
        }
        return themesProgress.get(themesProgress.size() - 1).getCourseThemeId();
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
