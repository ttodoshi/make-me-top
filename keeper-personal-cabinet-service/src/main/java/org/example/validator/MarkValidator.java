package org.example.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.coursemark.MarkDTO;
import org.example.dto.courseprogress.CourseThemeCompletionDTO;
import org.example.dto.courseprogress.CourseWithThemesProgress;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.exception.classes.homeworkEX.HomeworkNotCompletedException;
import org.example.exception.classes.keeperEX.DifferentKeeperException;
import org.example.exception.classes.markEX.ExplorerDoesNotNeedMarkException;
import org.example.exception.classes.markEX.UnexpectedMarkValueException;
import org.example.exception.classes.progressEX.PlanetAlreadyCompletedException;
import org.example.exception.classes.progressEX.UnexpectedCourseThemeException;
import org.example.model.Explorer;
import org.example.model.Keeper;
import org.example.model.Person;
import org.example.model.course.Course;
import org.example.model.course.CourseTheme;
import org.example.model.progress.CourseThemeCompletion;
import org.example.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MarkValidator {
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;
    private final CourseRepository courseRepository;
    private final CourseThemeRepository courseThemeRepository;
    private final CourseThemeCompletionRepository courseThemeCompletionRepository;
    private final HomeworkRepository homeworkRepository;

    public void validateCourseMarkRequest(MarkDTO courseMark) {
        if (!explorerRepository.existsById(courseMark.getExplorerId()))
            throw new ExplorerNotFoundException();
        if (isNotKeeperForThisExplorer(courseMark.getExplorerId()))
            throw new DifferentKeeperException();
        if (courseMark.getValue() < 1 || courseMark.getValue() > 5)
            throw new UnexpectedMarkValueException();
        if (!explorerNeedFinalAssessment(courseMark.getExplorerId()))
            throw new ExplorerDoesNotNeedMarkException(courseMark.getExplorerId());
    }

    private boolean isNotKeeperForThisExplorer(Integer explorerId) {
        final Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Explorer explorer = explorerRepository.getReferenceById(explorerId);
        Keeper keeper = keeperRepository.getKeeperForPersonOnCourse(explorer.getPersonId(), explorer.getCourseId());
        return !authenticatedPerson.getPersonId().equals(keeper.getPersonId());
    }

    private boolean explorerNeedFinalAssessment(Integer explorerId) {
        Person person = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return explorerRepository
                .getExplorersNeededFinalAssessmentByKeeperPersonId(person.getPersonId()).stream()
                .anyMatch(e -> e.getExplorerId().equals(explorerId));
    }

    public void validateThemeMarkRequest(Integer themeId, MarkDTO mark) {
        Explorer explorer = explorerRepository.findById(mark.getExplorerId())
                .orElseThrow(ExplorerNotFoundException::new);
        if (isNotKeeperForThisExplorer(explorer.getExplorerId()))
            throw new DifferentKeeperException();
        if (mark.getValue() < 1 || mark.getValue() > 5)
            throw new UnexpectedMarkValueException();
        Optional<CourseThemeCompletion> courseThemeProgressOptional = courseThemeCompletionRepository
                .findCourseThemeProgressByExplorerIdAndCourseThemeId(explorer.getExplorerId(), themeId);
        if (courseThemeProgressOptional.isPresent())
            throw new PlanetAlreadyCompletedException(courseThemeProgressOptional.get().getCourseThemeId());
        Integer currentThemeId = getCurrentCourseThemeId(explorer);
        if (!currentThemeId.equals(themeId))
            throw new UnexpectedCourseThemeException(themeId, currentThemeId);
        if (homeworkNotCompleted(themeId, explorer))
            throw new HomeworkNotCompletedException(themeId);
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
        Course course = courseRepository.findById(explorer.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException(explorer.getCourseId()));
        List<CourseThemeCompletionDTO> themesCompletion = new LinkedList<>();
        for (CourseTheme ct : courseThemeRepository.findCourseThemesByCourseIdOrderByCourseThemeNumberAsc(explorer.getCourseId())) {
            Boolean themeCompleted = courseThemeCompletionRepository
                    .findCourseThemeProgressByExplorerIdAndCourseThemeId(explorer.getExplorerId(), ct.getCourseThemeId()).isPresent();
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

    private boolean homeworkNotCompleted(Integer themeId, Explorer explorer) {
        return !(homeworkRepository.findAllByCourseThemeId(themeId).size() ==
                homeworkRepository.findAllCompletedByThemeId(themeId, explorer.getExplorerId()).size());
    }
}
