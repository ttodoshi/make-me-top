package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.courseprogress.CourseThemeCompletedDto;
import org.example.dto.theme.UpdateCourseThemeDto;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.coursethemeEX.CourseThemeAlreadyExistsException;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.example.exception.classes.coursethemeEX.ThemeClosedException;
import org.example.repository.CourseProgressRepository;
import org.example.repository.CourseRepository;
import org.example.repository.CourseThemeRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CourseThemeValidatorService {
    private final CourseRepository courseRepository;
    private final CourseThemeRepository courseThemeRepository;
    private final CourseProgressRepository courseProgressRepository;

    @Transactional(readOnly = true)
    public void validateGetThemeRequest(Integer courseThemeId) {
        if (!isThemeOpened(courseThemeId))
            throw new ThemeClosedException(courseThemeId);
    }

    private boolean isThemeOpened(Integer themeId) {
        Integer courseId = courseRepository.getCourseIdByThemeId(themeId)
                .orElseThrow(() -> new CourseThemeNotFoundException(themeId));
        List<CourseThemeCompletedDto> themesProgress = courseProgressRepository
                .getCourseProgress(courseId)
                .getThemesWithProgress();
        Optional<CourseThemeCompletedDto> themeCompletion = themesProgress
                .stream()
                .filter(t -> t.getCourseThemeId().equals(themeId))
                .findAny();
        Boolean themeCompleted = themeCompletion.orElseThrow(
                () -> new CourseThemeNotFoundException(themeId)
        ).getCompleted();
        return themeId.equals(getCurrentCourseThemeId(themesProgress)) || themeCompleted;
    }

    private Integer getCurrentCourseThemeId(List<CourseThemeCompletedDto> themesProgress) {
        for (CourseThemeCompletedDto planet : themesProgress) {
            if (!planet.getCompleted())
                return planet.getCourseThemeId();
        }
        return themesProgress.get(themesProgress.size() - 1).getCourseThemeId();
    }

    @Transactional(readOnly = true)
    public void validateGetThemesByCourseIdRequest(Integer courseId) {
        if (!courseRepository.existsById(courseId))
            throw new CourseNotFoundException(courseId);
    }

    @Transactional(readOnly = true)
    public void validatePutRequest(Integer themeId, UpdateCourseThemeDto theme) {
        if (!courseRepository.existsById(theme.getCourseId()))
            throw new CourseNotFoundException(theme.getCourseId());
        boolean themeTitleExists = courseThemeRepository.findCourseThemesByCourseIdOrderByCourseThemeNumber(
                        theme.getCourseId()).stream()
                .anyMatch(t -> t.getTitle().equals(theme.getTitle()) && !t.getCourseThemeId().equals(themeId));
        if (themeTitleExists)
            throw new CourseThemeAlreadyExistsException(theme.getTitle());
    }
}
