package org.example.homework.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.homework.dto.progress.CourseThemeCompletedDto;
import org.example.homework.exception.classes.homework.HomeworkRequestAlreadyClosedException;
import org.example.homework.exception.classes.progress.UnexpectedCourseThemeException;
import org.example.homework.model.HomeworkRequest;
import org.example.homework.model.HomeworkRequestStatusType;
import org.example.homework.repository.CourseProgressRepository;
import org.example.homework.service.HomeworkRequestStatusService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ExplorerHomeworkRequestValidatorService {
    private final CourseProgressRepository courseProgressRepository;

    private final HomeworkRequestStatusService homeworkRequestStatusService;

    @Transactional(readOnly = true)
    public void validateNewRequestVersion(HomeworkRequest request) {
        Integer closedStatusId = homeworkRequestStatusService.findHomeworkRequestStatusByStatus(
                HomeworkRequestStatusType.CLOSED
        ).getStatusId();
        if (request.getStatusId().equals(closedStatusId))
            throw new HomeworkRequestAlreadyClosedException(request.getRequestId());
    }

    @Transactional(readOnly = true)
    public void validateNewRequest(Integer themeId, Integer explorerId) {
        Integer currentThemeId = getCurrentCourseThemeId(explorerId);
        if (!currentThemeId.equals(themeId))
            throw new UnexpectedCourseThemeException(currentThemeId, themeId);
    }

    private Integer getCurrentCourseThemeId(Integer explorerId) {
        List<CourseThemeCompletedDto> themesProgress = courseProgressRepository
                .getCourseProgress(explorerId)
                .getThemesWithProgress();
        for (CourseThemeCompletedDto theme : themesProgress) {
            if (!theme.getCompleted())
                return theme.getCourseThemeId();
        }
        return themesProgress.get(themesProgress.size() - 1).getCourseThemeId();
    }
}
