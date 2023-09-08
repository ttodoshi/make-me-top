package org.example.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.dto.courseprogress.CourseThemeCompletedDto;
import org.example.dto.courseprogress.CurrentCourseProgressDto;
import org.example.dto.keeper.KeeperDto;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.example.exception.classes.explorerEX.ExplorerGroupNotFoundException;
import org.example.model.Explorer;
import org.example.model.course.Course;
import org.example.model.course.CourseTheme;
import org.example.repository.ExplorerGroupRepository;
import org.example.repository.ExplorerRepository;
import org.example.repository.KeeperRepository;
import org.example.repository.course.CourseRepository;
import org.example.repository.course.CourseThemeRepository;
import org.example.repository.courseprogress.CourseThemeCompletionRepository;
import org.example.service.CourseProgressService;
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
    private final ExplorerGroupRepository explorerGroupRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<CurrentCourseProgressDto> getCurrentCourseProgress(Integer personId) {
        Optional<CurrentCourseProgressDto> currentCourseProgressOptional = Optional.empty();
        Optional<Integer> currentSystemIdOptional = courseThemeCompletionRepository.getCurrentInvestigatedCourseId(personId);
        if (currentSystemIdOptional.isEmpty())
            return currentCourseProgressOptional;
        Integer currentSystemId = currentSystemIdOptional.get();
        Optional<Explorer> explorerOptional = explorerRepository.findExplorerByPersonIdAndCourseId(personId, currentSystemId);
        if (explorerOptional.isEmpty())
            return currentCourseProgressOptional;
        Explorer explorer = explorerOptional.get();
        double progress = Math.ceil(courseThemeCompletionRepository.getCourseProgress(explorer.getExplorerId(), currentSystemId) * 10) / 10;
        Integer currentThemeId = getCurrentCourseThemeId(explorer);
        CourseTheme currentTheme = courseThemeRepository.findById(currentThemeId).orElseThrow(() -> new CourseThemeNotFoundException(currentThemeId));
        Course currentCourse = courseRepository.getReferenceById(currentSystemId);
        KeeperDto keeper = keeperRepository.getKeeperForExplorer(explorer.getExplorerId());
        return Optional.of(new CurrentCourseProgressDto(explorer.getExplorerId(), explorer.getGroupId(), currentTheme.getCourseThemeId(), currentTheme.getTitle(), currentCourse.getCourseId(), currentCourse.getTitle(), keeper, progress));
    }

    private Integer getCurrentCourseThemeId(Explorer explorer) {
        List<CourseThemeCompletedDto> themesProgress = getCourseWithThemesProgress(explorer);
        for (CourseThemeCompletedDto planet : themesProgress) {
            if (!planet.getCompleted())
                return planet.getCourseThemeId();
        }
        return themesProgress.get(themesProgress.size() - 1).getCourseThemeId();
    }

    private List<CourseThemeCompletedDto> getCourseWithThemesProgress(Explorer explorer) {
        Integer courseId = explorerGroupRepository.findById(explorer.getGroupId())
                .orElseThrow(() -> new ExplorerGroupNotFoundException(explorer.getGroupId()))
                .getCourseId();
        return courseThemeRepository
                .findCourseThemesByCourseIdOrderByCourseThemeNumberAsc(courseId)
                .stream()
                .map(t -> {
                            boolean planetCompleted = courseThemeCompletionRepository
                                    .findCourseThemeProgressByExplorerIdAndCourseThemeId(explorer.getExplorerId(), t.getCourseThemeId())
                                    .isPresent();
                            return new CourseThemeCompletedDto(
                                    t.getCourseThemeId(), t.getTitle(), planetCompleted);
                        }
                ).collect(Collectors.toList());
    }
}
