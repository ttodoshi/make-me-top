package org.example.progress.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.grpc.ExplorersService;
import org.example.progress.dto.course.CourseDto;
import org.example.progress.dto.mark.ThemeMarkDto;
import org.example.progress.dto.progress.CourseThemeCompletedDto;
import org.example.progress.dto.progress.CourseWithThemesProgressDto;
import org.example.progress.model.CourseThemeCompletion;
import org.example.progress.repository.CourseThemeCompletionRepository;
import org.example.progress.service.CourseService;
import org.example.progress.service.CourseThemesProgressService;
import org.example.progress.service.ExplorerGroupService;
import org.example.progress.service.PlanetService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseThemesProgressServiceImpl implements CourseThemesProgressService {
    private final CourseThemeCompletionRepository courseThemeCompletionRepository;

    private final CourseService courseService;
    private final ExplorerGroupService explorerGroupService;
    private final PlanetService planetService;

    private final ModelMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public CourseWithThemesProgressDto getThemesProgress(String authorizationHeader, ExplorersService.Explorer explorer) {
        Long courseId = explorerGroupService.findById(
                authorizationHeader, explorer.getGroupId()
        ).getCourseId();
        CourseDto course = courseService.findById(authorizationHeader, courseId);

        List<CourseThemeCompletedDto> themesProgress = planetService.findPlanetsBySystemId(
                        authorizationHeader, courseId
                ).stream()
                .map(p -> {
                    boolean completed = courseThemeCompletionRepository
                            .findCourseThemeCompletionByExplorerIdAndCourseThemeId(explorer.getExplorerId(), p.getPlanetId()).isPresent();
                    return new CourseThemeCompletedDto(p.getPlanetId(), p.getPlanetName(), p.getPlanetNumber(), completed);
                }).collect(Collectors.toList());
        return new CourseWithThemesProgressDto(courseId, course.getTitle(), themesProgress);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, List<ThemeMarkDto>> getExplorersThemesMarks(List<Long> explorerIds) {
        List<CourseThemeCompletion> courseThemeCompletions = courseThemeCompletionRepository
                .findCourseThemeCompletionsByExplorerIdIn(explorerIds);
        return courseThemeCompletions.stream()
                .collect(Collectors.groupingBy(
                        CourseThemeCompletion::getExplorerId,
                        Collectors.mapping(
                                m -> mapper.map(m, ThemeMarkDto.class),
                                Collectors.toList()
                        )
                ));
    }
}
