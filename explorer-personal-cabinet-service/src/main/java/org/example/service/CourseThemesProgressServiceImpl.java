package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.courseprogress.CourseThemeCompletedDto;
import org.example.dto.courseprogress.CourseWithThemesProgressDto;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.model.Explorer;
import org.example.model.course.Course;
import org.example.repository.ExplorerGroupRepository;
import org.example.repository.course.CourseRepository;
import org.example.repository.course.CourseThemeRepository;
import org.example.repository.courseprogress.CourseThemeCompletionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseThemesProgressServiceImpl implements CourseThemesProgressService {
    private final CourseRepository courseRepository;
    private final CourseThemeRepository courseThemeRepository;
    private final CourseThemeCompletionRepository courseThemeCompletionRepository;
    private final ExplorerGroupRepository explorerGroupRepository;

    @Override
    @Transactional(readOnly = true)
    public CourseWithThemesProgressDto getThemesProgress(Explorer explorer) {
        Integer courseId = explorerGroupRepository
                .getReferenceById(explorer.getGroupId()).getCourseId();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
        List<CourseThemeCompletedDto> themesProgress = courseThemeRepository
                .findCourseThemesByCourseIdOrderByCourseThemeNumberAsc(courseId)
                .stream()
                .map(
                        t -> {
                            boolean completed = courseThemeCompletionRepository
                                    .findCourseThemeProgressByExplorerIdAndCourseThemeId(explorer.getExplorerId(), t.getCourseThemeId()).isPresent();
                            return new CourseThemeCompletedDto(t.getCourseThemeId(), t.getTitle(), completed);
                        }
                ).collect(Collectors.toList());
        return new CourseWithThemesProgressDto(courseId, course.getTitle(), themesProgress);
    }
}
