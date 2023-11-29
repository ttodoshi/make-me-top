package org.example.progress.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.grpc.ExplorersService;
import org.example.progress.dto.course.CourseDto;
import org.example.progress.dto.progress.CourseThemeCompletedDto;
import org.example.progress.dto.progress.CourseWithThemesProgressDto;
import org.example.progress.exception.classes.course.CourseNotFoundException;
import org.example.progress.repository.CourseRepository;
import org.example.progress.repository.CourseThemeCompletionRepository;
import org.example.progress.repository.ExplorerGroupRepository;
import org.example.progress.repository.PlanetRepository;
import org.example.progress.service.CourseThemesProgressService;
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
    public CourseWithThemesProgressDto getThemesProgress(ExplorersService.Explorer explorer) {
        Long courseId = explorerGroupRepository
                .getReferenceById(explorer.getGroupId()).getCourseId();
        CourseDto course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        List<CourseThemeCompletedDto> themesProgress = planetRepository.findPlanetsBySystemId(courseId)
                .stream()
                .map(p -> {
                    boolean completed = courseThemeCompletionRepository
                            .findCourseThemeProgressByExplorerIdAndCourseThemeId(explorer.getExplorerId(), p.getPlanetId()).isPresent();
                    return new CourseThemeCompletedDto(p.getPlanetId(), p.getPlanetName(), p.getPlanetNumber(), completed);
                }).collect(Collectors.toList());
        return new CourseWithThemesProgressDto(courseId, course.getTitle(), themesProgress);
    }
}
