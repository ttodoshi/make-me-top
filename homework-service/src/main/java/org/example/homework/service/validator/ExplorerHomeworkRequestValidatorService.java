package org.example.homework.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.homework.dto.progress.CourseThemeCompletedDto;
import org.example.homework.exception.homework.HomeworkRequestAlreadyClosedException;
import org.example.homework.exception.progress.UnexpectedCourseThemeException;
import org.example.homework.model.HomeworkRequest;
import org.example.homework.model.HomeworkRequestStatusType;
import org.example.homework.service.CourseProgressService;
import org.example.homework.service.HomeworkRequestStatusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExplorerHomeworkRequestValidatorService {
    private final CourseProgressService courseProgressService;
    private final HomeworkRequestStatusService homeworkRequestStatusService;

    @Transactional(readOnly = true)
    public void validateNewRequestVersion(HomeworkRequest request) {
        Long closedStatusId = homeworkRequestStatusService.findHomeworkRequestStatusByStatus(
                HomeworkRequestStatusType.CLOSED
        ).getStatusId();
        if (request.getStatusId().equals(closedStatusId)) {
            log.warn("homework request {} already closed", request.getRequestId());
            throw new HomeworkRequestAlreadyClosedException(request.getRequestId());
        }
    }

    @Transactional(readOnly = true)
    public void validateNewRequest(String authorizationHeader, Long themeId, Long explorerId) {
        Long currentThemeId = getCurrentCourseThemeId(authorizationHeader, explorerId);
        if (!currentThemeId.equals(themeId)) {
            log.warn("theme {} is not current theme for explorer {}", themeId, explorerId);
            throw new UnexpectedCourseThemeException(currentThemeId, themeId);
        }
    }

    private Long getCurrentCourseThemeId(String authorizationHeader, Long explorerId) {
        List<CourseThemeCompletedDto> themesProgress = courseProgressService
                .getCourseProgress(authorizationHeader, explorerId)
                .getThemesWithProgress();
        for (CourseThemeCompletedDto theme : themesProgress) {
            if (!theme.getCompleted())
                return theme.getCourseThemeId();
        }
        return themesProgress.get(themesProgress.size() - 1).getCourseThemeId();
    }
}
