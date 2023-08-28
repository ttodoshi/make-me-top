package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.courseprogress.CourseThemeCompletionDTO;
import org.example.dto.courseprogress.CourseWithThemesProgress;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.model.Explorer;
import org.example.model.course.Course;
import org.example.repository.course.CourseRepository;
import org.example.repository.course.CourseThemeRepository;
import org.example.repository.courseprogress.CourseThemeCompletionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseThemesProgressService {
    private final CourseRepository courseRepository;
    private final CourseThemeRepository courseThemeRepository;
    private final CourseThemeCompletionRepository courseThemeCompletionRepository;

    public CourseWithThemesProgress getThemesProgress(Explorer explorer) {
        Course course = courseRepository.findById(explorer.getCourseId()).orElseThrow(() -> new CourseNotFoundException(explorer.getCourseId()));
        List<CourseThemeCompletionDTO> themesProgress = courseThemeRepository
                .findCourseThemesByCourseIdOrderByCourseThemeNumberAsc(explorer.getCourseId())
                .stream()
                .map(
                        t -> {
                            boolean completed = courseThemeCompletionRepository
                                    .findCourseThemeProgressByExplorerIdAndCourseThemeId(explorer.getExplorerId(), t.getCourseThemeId()).isPresent();
                            return new CourseThemeCompletionDTO(t.getCourseThemeId(), t.getTitle(), completed);
                        }
                ).collect(Collectors.toList());
        return new CourseWithThemesProgress(explorer.getCourseId(), course.getTitle(), themesProgress);
    }
}
