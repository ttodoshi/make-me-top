package org.example.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.dto.course.CourseDto;
import org.example.dto.explorer.ExplorerDto;
import org.example.dto.progress.CourseThemeCompletedDto;
import org.example.dto.progress.CourseWithThemesProgressDto;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.repository.CourseRepository;
import org.example.repository.CourseThemeCompletionRepository;
import org.example.repository.ExplorerGroupRepository;
import org.example.repository.PlanetRepository;
import org.example.service.CourseThemesProgressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseThemesProgressServiceImpl implements CourseThemesProgressService {
    private final CourseRepository courseRepository;
    private final CourseThemeCompletionRepository courseThemeCompletionRepository;
    private final ExplorerGroupRepository explorerGroupRepository;
    private final PlanetRepository planetRepository;

    @Override
    @Transactional(readOnly = true)
    public CourseWithThemesProgressDto getThemesProgress(ExplorerDto explorer) {
        Integer courseId = explorerGroupRepository
                .getReferenceById(explorer.getGroupId()).getCourseId();
        CourseDto course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
        List<CourseThemeCompletedDto> themesProgress = planetRepository.findPlanetsBySystemId(courseId)
                .stream()
                .map(p -> {
                    boolean completed = courseThemeCompletionRepository
                            .findCourseThemeProgressByExplorerIdAndCourseThemeId(explorer.getExplorerId(), p.getPlanetId()).isPresent();
                    return new CourseThemeCompletedDto(p.getPlanetId(), p.getPlanetName(), completed);
                }).collect(Collectors.toList());
        return new CourseWithThemesProgressDto(courseId, course.getTitle(), themesProgress);
    }
}
