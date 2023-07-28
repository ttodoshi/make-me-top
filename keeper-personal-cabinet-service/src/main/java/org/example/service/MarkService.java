package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.coursemark.MarkDTO;
import org.example.dto.systemprogress.CourseThemeProgressDTO;
import org.example.dto.systemprogress.PlanetCompletionDTO;
import org.example.dto.systemprogress.SystemWithPlanetsProgress;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
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
import org.example.model.progress.CourseMark;
import org.example.model.progress.CourseThemeCompletion;
import org.example.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MarkService {
    private final CourseMarkRepository courseMarkRepository;
    private final ExplorerRepository explorerRepository;
    private final PlanetProgressRepository planetProgressRepository;
    private final CourseRepository courseRepository;
    private final CourseThemeRepository courseThemeRepository;
    private final KeeperRepository keeperRepository;

    private final ModelMapper mapper;

    @Transactional
    public CourseMark setCourseMark(MarkDTO courseMark) {
        if (!explorerRepository.existsById(courseMark.getExplorerId()))
            throw new ExplorerNotFoundException();
        if (isNotKeeperForThisExplorer(courseMark.getExplorerId()))
            throw new DifferentKeeperException();
        if (courseMark.getValue() < 1 || courseMark.getValue() > 5)
            throw new UnexpectedMarkValueException();
        if (explorerNeedFinalAssessment(courseMark.getExplorerId()))
            return courseMarkRepository.save(
                    mapper.map(courseMark, CourseMark.class)
            );
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

    @Transactional
    public Map<String, Object> setThemeMark(Integer planetId, MarkDTO mark) {
        saveProgress(planetId, mark);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Планета " + planetId +
                " завершена исследователем " + mark.getExplorerId());
        return response;
    }

    private void saveProgress(Integer themeId, MarkDTO mark) {
        Explorer explorer = explorerRepository.findById(mark.getExplorerId()).orElseThrow(ExplorerNotFoundException::new);
        if (isNotKeeperForThisExplorer(mark.getExplorerId()))
            throw new DifferentKeeperException();
        if (mark.getValue() < 1 || mark.getValue() > 5)
            throw new UnexpectedMarkValueException();
        Optional<CourseThemeCompletion> courseThemeProgressOptional = planetProgressRepository
                .findCourseThemeProgressByExplorerIdAndCourseThemeId(explorer.getExplorerId(), themeId);
        if (courseThemeProgressOptional.isPresent() && courseThemeProgressOptional.get().getCompleted())
            throw new PlanetAlreadyCompletedException(courseThemeProgressOptional.get().getCourseThemeId());
        Integer currentThemeId = getCurrentCourseThemeId(explorer);
        if (!currentThemeId.equals(themeId))
            throw new UnexpectedCourseThemeException(themeId, currentThemeId);
        planetProgressRepository.save(mapper.map(new CourseThemeProgressDTO(
                        explorer.getExplorerId(), themeId, true, mark.getValue()),
                CourseThemeCompletion.class));
    }

    private Integer getCurrentCourseThemeId(Explorer explorer) {
        List<PlanetCompletionDTO> planetsProgress = getPlanetsProgress(explorer).getPlanetsWithProgress();
        for (PlanetCompletionDTO planet : planetsProgress) {
            if (!planet.getCompleted())
                return planet.getCourseThemeId();
        }
        return planetsProgress.get(planetsProgress.size() - 1).getCourseThemeId();
    }

    private SystemWithPlanetsProgress getPlanetsProgress(Explorer explorer) {
        Course course = courseRepository.findById(explorer.getCourseId()).orElseThrow(() -> new CourseNotFoundException(explorer.getCourseId()));
        List<PlanetCompletionDTO> planetsCompletion = new LinkedList<>();
        for (CourseTheme ct : courseThemeRepository.findCourseThemesByCourseIdOrderByCourseThemeNumberAsc(explorer.getCourseId())) {
            Boolean planetCompleted = planetProgressRepository.findCourseThemeProgressByExplorerIdAndCourseThemeId(explorer.getExplorerId(), ct.getCourseThemeId()).isPresent();
            planetsCompletion.add(
                    new PlanetCompletionDTO(ct.getCourseThemeId(), ct.getTitle(), planetCompleted)
            );
        }
        return SystemWithPlanetsProgress.builder()
                .courseId(explorer.getCourseId())
                .title(course.getTitle())
                .planetsWithProgress(planetsCompletion)
                .build();
    }
}
