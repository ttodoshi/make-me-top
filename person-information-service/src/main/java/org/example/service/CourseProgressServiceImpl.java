package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.courseprogress.CourseThemeCompletionDTO;
import org.example.dto.courseprogress.CurrentCourseProgressDTO;
import org.example.dto.keeper.KeeperDTO;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.example.model.Explorer;
import org.example.model.course.Course;
import org.example.model.course.CourseTheme;
import org.example.repository.ExplorerRepository;
import org.example.repository.KeeperRepository;
import org.example.repository.course.CourseRepository;
import org.example.repository.course.CourseThemeRepository;
import org.example.repository.courseprogress.CourseThemeCompletionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseProgressServiceImpl implements CourseProgressService {
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;
    private final CourseThemeCompletionRepository courseThemeCompletionRepository;
    private final CourseThemeRepository courseThemeRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<CurrentCourseProgressDTO> getCurrentCourseProgress(Integer personId) {
        Optional<CurrentCourseProgressDTO> currentCourseProgressOptional = Optional.empty();
        Optional<Integer> currentSystemIdOptional = courseThemeCompletionRepository.getCurrentInvestigatedCourseId(personId);
        if (currentSystemIdOptional.isEmpty())
            return currentCourseProgressOptional;
        Integer currentSystemId = currentSystemIdOptional.get();
        Optional<Explorer> explorerOptional = explorerRepository.findExplorerByPersonIdAndCourseId(personId, currentSystemId);
        if (explorerOptional.isEmpty())
            return currentCourseProgressOptional;
        Explorer explorer = explorerOptional.get();
        Double progress = courseThemeCompletionRepository.getCourseProgress(explorer.getExplorerId(), currentSystemId);
        Integer currentThemeId = getCurrentCourseThemeId(explorer);
        CourseTheme currentTheme = courseThemeRepository.findById(currentThemeId).orElseThrow(() -> new CourseThemeNotFoundException(currentThemeId));
        Course currentCourse = courseRepository.getReferenceById(currentSystemId);
        KeeperDTO keeper = keeperRepository.getKeeperForPersonOnCourse(personId, currentSystemId);
        return Optional.of(new CurrentCourseProgressDTO(explorer.getExplorerId(), currentTheme.getCourseThemeId(), currentTheme.getTitle(), currentCourse.getCourseId(), currentCourse.getTitle(), keeper, progress));
    }

    private Integer getCurrentCourseThemeId(Explorer explorer) {
        List<CourseThemeCompletionDTO> themesProgress = getCourseWithThemesProgress(explorer);
        for (CourseThemeCompletionDTO planet : themesProgress) {
            if (!planet.getCompleted())
                return planet.getCourseThemeId();
        }
        return themesProgress.get(themesProgress.size() - 1).getCourseThemeId();
    }

    private List<CourseThemeCompletionDTO> getCourseWithThemesProgress(Explorer explorer) {
        return courseThemeRepository
                .findCourseThemesByCourseIdOrderByCourseThemeNumberAsc(explorer.getCourseId())
                .stream()
                .map(t -> {
                            boolean planetCompleted = courseThemeCompletionRepository
                                    .findCourseThemeProgressByExplorerIdAndCourseThemeId(explorer.getExplorerId(), t.getCourseThemeId())
                                    .isPresent();
                            return new CourseThemeCompletionDTO(
                                    t.getCourseThemeId(), t.getTitle(), planetCompleted);
                        }
                ).collect(Collectors.toList());
    }
}
