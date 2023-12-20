package org.example.progress.service;

import lombok.RequiredArgsConstructor;
import org.example.grpc.ExplorerGroupsService;
import org.example.grpc.ExplorersService;
import org.example.grpc.PeopleService;
import org.example.progress.dto.galaxy.GetGalaxyDto;
import org.example.progress.dto.planet.PlanetDto;
import org.example.progress.dto.progress.*;
import org.example.progress.dto.system.GetStarSystemWithDependenciesDto;
import org.example.progress.dto.system.SystemDependencyModelDto;
import org.example.progress.exception.classes.explorer.ExplorerNotFoundException;
import org.example.progress.model.CourseMark;
import org.example.progress.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService {
    private final CourseMarkRepository courseMarkRepository;
    private final CourseThemeCompletionRepository courseThemeCompletionRepository;
    private final ExplorerRepository explorerRepository;
    private final ExplorerGroupRepository explorerGroupRepository;
    private final PlanetRepository planetRepository;
    private final GalaxyRepository galaxyRepository;

    private final CourseThemesProgressService courseThemesProgressService;
    private final PersonService personService;

    @Transactional(readOnly = true)
    public CoursesStateDto getCoursesProgressForCurrentUser(Long galaxyId) {
        PeopleService.Person authenticatedPerson = personService.getAuthenticatedPerson();

        Set<Long> openedCourses = new LinkedHashSet<>();
        Set<CourseWithProgressDto> studiedCourses = new LinkedHashSet<>();
        Set<Long> closedCourses = new LinkedHashSet<>();

        GetGalaxyDto galaxy = galaxyRepository.findGalaxyById(galaxyId);

        List<GetStarSystemWithDependenciesDto> systems = galaxy.getOrbitList()
                .stream()
                .flatMap(o -> o.getSystemWithDependenciesList().stream())
                .collect(Collectors.toList());

        Map<Long, ExplorersService.Explorer> explorers = explorerRepository
                .findExplorersByPersonIdAndGroupCourseIdIn(
                        authenticatedPerson.getPersonId(),
                        systems.stream().map(GetStarSystemWithDependenciesDto::getSystemId).collect(Collectors.toList())
                ).getExplorerWithCourseIdMapMap();

        for (GetStarSystemWithDependenciesDto system : systems) {
            if (explorers.containsKey(system.getSystemId())) {
                studiedCourses.add(
                        new CourseWithProgressDto(
                                system.getSystemId(),
                                getCourseProgress(explorers.get(system.getSystemId()).getExplorerId(), system.getSystemId())
                        )
                );
            } else if (hasUncompletedParents(explorers, system)) {
                closedCourses.add(system.getSystemId());
            } else {
                openedCourses.add(system.getSystemId());
            }
        }

        return CoursesStateDto.builder()
                .personId(authenticatedPerson.getPersonId())
                .firstName(authenticatedPerson.getFirstName())
                .lastName(authenticatedPerson.getLastName())
                .patronymic(authenticatedPerson.getPatronymic())
                .openedCourses(openedCourses)
                .studiedCourses(studiedCourses)
                .closedCourses(closedCourses)
                .build();
    }

    public CourseWithThemesProgressDto getExplorerThemesProgress(Long explorerId) {
        ExplorersService.Explorer explorer = explorerRepository.findById(explorerId)
                .orElseThrow(() -> new ExplorerNotFoundException(explorerId));
        return courseThemesProgressService.getThemesProgress(explorer);
    }

    private boolean hasUncompletedParents(Map<Long, ExplorersService.Explorer> explorers, GetStarSystemWithDependenciesDto system) {
        for (SystemDependencyModelDto systemDependency : getParentDependencies(system)) {
            if (!explorers.containsKey(systemDependency.getSystemId()) || getCourseProgress(explorers.get(systemDependency.getSystemId()).getExplorerId(), systemDependency.getSystemId()) < 100) {
                return true;
            } else if (systemDependency.getIsAlternative()) {
                return false;
            }
        }
        return false;
    }

    private double getCourseProgress(Long explorerId, Long systemId) {
        return Math.ceil(
                courseThemeCompletionRepository.getCourseProgress(
                        explorerId, planetRepository.findPlanetsBySystemId(systemId).size()) * 10
        ) / 10;
    }

    private List<SystemDependencyModelDto> getParentDependencies(GetStarSystemWithDependenciesDto systemWithDependencies) {
        return systemWithDependencies.getDependencyList()
                .stream()
                .filter(s -> s.getType().equals("parent"))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Long> getExplorerIdsNeededFinalAssessment(List<Long> explorerIds) {
        List<Long> explorersWithFinalAssessment = getExplorerIdsWithFinalAssessment(explorerIds);

        Map<Long, ExplorersService.Explorer> explorers = explorerRepository
                .findExplorersByExplorerIdIn(explorerIds);
        Map<Long, ExplorerGroupsService.ExplorerGroup> explorerGroups = explorerGroupRepository
                .findExplorerGroupsByGroupIdIn(
                        explorers.values().stream().map(ExplorersService.Explorer::getGroupId).collect(Collectors.toList())
                );

        Map<Long, List<PlanetDto>> planets = planetRepository.findPlanetsBySystemIdIn(
                explorerGroups.values().stream().map(ExplorerGroupsService.ExplorerGroup::getCourseId).distinct().collect(Collectors.toList())
        );

        return explorerIds.stream()
                .filter(eId -> !explorersWithFinalAssessment.contains(eId))
                .filter(eId -> {
                    Long explorerCourseId = explorerGroups.get(
                            explorers.get(eId).getGroupId()
                    ).getCourseId();
                    return courseThemeCompletionRepository
                            .getCourseProgress(
                                    eId,
                                    planets.get(explorerCourseId).size()
                            ) == 100;
                })
                .collect(Collectors.toList());
    }

    public List<Long> getExplorerIdsWithFinalAssessment(List<Long> explorerIds) {
        return courseMarkRepository.findExplorerIdsWithFinalAssessment(explorerIds);
    }

    @Transactional(readOnly = true)
    public ExplorerProgressDto getExplorerCourseProgress(Long courseId) {
        ExplorersService.Explorer explorer = explorerRepository.findExplorerByPersonIdAndGroup_CourseId(
                personService.getAuthenticatedPersonId(),
                courseId
        ).orElseThrow(ExplorerNotFoundException::new);

        CourseWithThemesProgressDto themesProgress = courseThemesProgressService.getThemesProgress(explorer);

        return new ExplorerProgressDto(
                explorer.getExplorerId(),
                explorer.getGroupId(),
                getCurrentCourseThemeId(themesProgress),
                themesProgress,
                courseMarkRepository
                        .findById(explorer.getExplorerId())
                        .map(CourseMark::getValue)
                        .orElse(null)
        );
    }

    private Long getCurrentCourseThemeId(CourseWithThemesProgressDto courseProgress) {
        List<CourseThemeCompletedDto> themesProgress = courseProgress.getThemesWithProgress();
        for (CourseThemeCompletedDto planet : themesProgress) {
            if (!planet.getCompleted())
                return planet.getCourseThemeId();
        }
        return themesProgress.get(themesProgress.size() - 1).getCourseThemeId();
    }
}
