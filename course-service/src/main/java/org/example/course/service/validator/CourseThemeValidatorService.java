package org.example.course.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.course.exception.classes.course.CourseNotFoundException;
import org.example.course.exception.classes.theme.CourseThemeAlreadyExistsException;
import org.example.course.exception.classes.theme.CourseThemeNotFoundException;
import org.example.course.exception.classes.theme.ThemeClosedException;
import org.example.course.exception.classes.explorer.ExplorerNotFoundException;
import org.example.course.dto.courseprogress.CourseThemeCompletedDto;
import org.example.course.dto.theme.UpdateCourseThemeDto;
import org.example.course.repository.CourseRepository;
import org.example.course.repository.CourseThemeRepository;
import org.example.course.repository.ExplorerRepository;
import org.example.course.service.CourseProgressService;
import org.example.course.service.PersonService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CourseThemeValidatorService {
    private final CourseRepository courseRepository;
    private final CourseThemeRepository courseThemeRepository;

    private final ExplorerRepository explorerRepository;
    private final CourseProgressService courseProgressService;
    private final PersonService personService;

    @Transactional(readOnly = true)
    public void validateGetThemeRequest(Integer courseThemeId) {
        if (!isThemeOpened(courseThemeId))
            throw new ThemeClosedException(courseThemeId);
    }

    private boolean isThemeOpened(Integer themeId) {
        Integer courseId = courseThemeRepository.findById(themeId)
                .orElseThrow(() -> new CourseThemeNotFoundException(themeId))
                .getCourseId();
        List<CourseThemeCompletedDto> themesProgress = courseProgressService
                .getCourseProgress(
                        explorerRepository.findExplorerByPersonIdAndGroup_CourseId(
                                        personService.getAuthenticatedPersonId(),
                                        courseId
                                ).orElseThrow(ExplorerNotFoundException::new)
                                .getExplorerId()
                )
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
