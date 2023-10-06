package org.example.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.dto.course.CourseDto;
import org.example.dto.explorer.ExplorerDto;
import org.example.dto.progress.CourseThemeCompletedDto;
import org.example.dto.progress.CourseWithThemesProgressDto;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.repository.CourseRepository;
import org.example.repository.CourseThemeCompletionRepository;
import org.example.repository.CourseThemeRepository;
import org.example.repository.ExplorerGroupRepository;
import org.example.service.CourseThemesProgressService;
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
    public CourseWithThemesProgressDto getThemesProgress(ExplorerDto explorer) {
        Integer courseId = explorerGroupRepository
                .getReferenceById(explorer.getGroupId()).getCourseId();
        CourseDto course = courseRepository.findById(courseId)
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
