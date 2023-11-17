package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.progress.CourseThemeCompletedDto;
import org.example.exception.classes.homeworkEX.HomeworkAlreadyCheckingException;
import org.example.exception.classes.homeworkEX.HomeworkRequestAlreadyClosedException;
import org.example.exception.classes.planetEX.PlanetNotFoundException;
import org.example.exception.classes.progressEX.UnexpectedCourseThemeException;
import org.example.exception.classes.requestEX.StatusNotFoundException;
import org.example.grpc.ExplorersService;
import org.example.model.HomeworkRequest;
import org.example.model.HomeworkRequestStatusType;
import org.example.repository.CourseProgressRepository;
import org.example.repository.HomeworkRequestStatusRepository;
import org.example.repository.PlanetRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ExplorerHomeworkRequestValidatorService {
    private final PlanetRepository planetRepository;
    private final HomeworkRequestStatusRepository homeworkRequestStatusRepository;
    private final CourseProgressRepository courseProgressRepository;


    @Transactional(readOnly = true)
    public void validateExistingRequest(HomeworkRequest request) {
        if (request.getStatusId().equals(getStatusId(HomeworkRequestStatusType.CHECKING)))
            throw new HomeworkAlreadyCheckingException(request.getHomeworkId());
        if (request.getStatusId().equals(getStatusId(HomeworkRequestStatusType.CLOSED)))
            throw new HomeworkRequestAlreadyClosedException(request.getRequestId());
    }

    @Transactional(readOnly = true)
    public void validateNewRequest(Integer themeId, ExplorersService.Explorer explorer) {
        Integer currentThemeId = getCurrentCourseThemeId(explorer);
        if (!currentThemeId.equals(themeId))
            throw new UnexpectedCourseThemeException(currentThemeId, themeId);
    }

    private Integer getCurrentCourseThemeId(ExplorersService.Explorer explorer) {
        List<CourseThemeCompletedDto> themesProgress = courseProgressRepository
                .getCourseProgress(explorer.getExplorerId())
                .getThemesWithProgress();
        for (CourseThemeCompletedDto theme : themesProgress) {
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

    @Transactional(readOnly = true)
    public void validateGetHomeworkRequests(Integer themeId) {
        if (!planetRepository.existsById(themeId))
            throw new PlanetNotFoundException(themeId);
    }
}
