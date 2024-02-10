package org.example.progress.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.grpc.ExplorerGroupsService;
import org.example.grpc.ExplorersService;
import org.example.grpc.PeopleService;
import org.example.progress.config.security.RoleService;
import org.example.progress.dto.galaxy.GetGalaxyDto;
import org.example.progress.dto.planet.PlanetDto;
import org.example.progress.dto.progress.*;
import org.example.progress.dto.system.GetStarSystemWithDependenciesDto;
import org.example.progress.dto.system.SystemDependencyModelDto;
import org.example.progress.model.CourseMark;
import org.example.progress.repository.CourseMarkRepository;
import org.example.progress.repository.CourseThemeCompletionRepository;
import org.example.progress.service.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressServiceImpl implements ProgressService {
    private final CourseMarkRepository courseMarkRepository;
    private final CourseThemeCompletionRepository courseThemeCompletionRepository;

    private final PersonService personService;
    private final ExplorerService explorerService;
    private final ExplorerGroupService explorerGroupService;
    private final PlanetService planetService;
    private final GalaxyService galaxyService;
    private final CourseThemesProgressService courseThemesProgressService;

    private final RoleService roleService;

    @Override
    @Transactional(readOnly = true)
    public CoursesStateDto getCoursesProgressForCurrentUser(String authorizationHeader, Long authenticatedPersonId, Long galaxyId) {
        PeopleService.Person authenticatedPerson = personService.findPersonById(
                authorizationHeader, authenticatedPersonId
        );

        Set<Long> openedCourses = new LinkedHashSet<>();
        Set<CourseWithProgressDto> studiedCourses = new LinkedHashSet<>();
        Set<Long> closedCourses = new LinkedHashSet<>();

        GetGalaxyDto galaxy = galaxyService.findGalaxyById(galaxyId);

        List<GetStarSystemWithDependenciesDto> systems = galaxy.getOrbitList()
                .stream()
                .flatMap(o -> o.getSystemWithDependenciesList().stream())
                .collect(Collectors.toList());

        Map<Long, ExplorersService.Explorer> explorers = explorerService.findExplorersByPersonIdAndGroupCourseIdIn(
                authorizationHeader,
                authenticatedPersonId,
                systems.stream().map(GetStarSystemWithDependenciesDto::getSystemId).collect(Collectors.toList())
        );

        for (GetStarSystemWithDependenciesDto system : systems) {
            if (explorers.containsKey(system.getSystemId())) {
                studiedCourses.add(
                        new CourseWithProgressDto(
                                system.getSystemId(),
                                getCourseProgress(authorizationHeader, explorers.get(system.getSystemId()).getExplorerId(), system.getSystemId())
                        )
                );
            } else if (hasUncompletedParents(authorizationHeader, explorers, system)) {
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

    @Override
    public CourseWithThemesProgressDto getExplorerThemesProgress(String authorizationHeader, Long explorerId) {
        ExplorersService.Explorer explorer = explorerService.findById(authorizationHeader, explorerId);
        return courseThemesProgressService.getThemesProgress(authorizationHeader, explorer);
    }

    private boolean hasUncompletedParents(String authorizationHeader, Map<Long, ExplorersService.Explorer> explorers, GetStarSystemWithDependenciesDto system) {
        for (SystemDependencyModelDto systemDependency : getParentDependencies(system)) {
            if (!explorers.containsKey(systemDependency.getSystemId()) ||
                    getCourseProgress(authorizationHeader, explorers.get(systemDependency.getSystemId()).getExplorerId(), systemDependency.getSystemId()) < 100) {
                return true;
            } else if (systemDependency.getIsAlternative()) {
                return false;
            }
        }
        return false;
    }

    private double getCourseProgress(String authorizationHeader, Long explorerId, Long systemId) {
        return Math.ceil(
                courseThemeCompletionRepository.getCourseProgress(
                        explorerId, planetService.findPlanetsBySystemId(authorizationHeader, systemId).size()) * 10
        ) / 10;
    }

    private List<SystemDependencyModelDto> getParentDependencies(GetStarSystemWithDependenciesDto systemWithDependencies) {
        return systemWithDependencies.getDependencyList()
                .stream()
                .filter(s -> s.getType().equals("parent"))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getExplorerIdsNeededFinalAssessment(String authorizationHeader, Long authenticatedPersonId, List<Long> explorerIds) {
        if (!roleService.isExplorersKeeper(authorizationHeader, authenticatedPersonId, explorerIds)) {
            throw new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера");
        }
        List<Long> explorersWithFinalAssessment = getExplorerIdsWithFinalAssessment(explorerIds);

        Map<Long, ExplorersService.Explorer> explorers = explorerService
                .findExplorersByExplorerIdIn(authorizationHeader, explorerIds);
        Map<Long, ExplorerGroupsService.ExplorerGroup> explorerGroups = explorerGroupService
                .findExplorerGroupsByGroupIdIn(
                        authorizationHeader,
                        explorers.values().stream().map(ExplorersService.Explorer::getGroupId).collect(Collectors.toList())
                );

        Map<Long, List<PlanetDto>> planets = planetService.findPlanetsBySystemIdIn(
                authorizationHeader,
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

    @Override
    public List<Long> getExplorerIdsWithFinalAssessment(List<Long> explorerIds) {
        return courseMarkRepository.findExplorerIdsWithFinalAssessment(explorerIds);
    }

    @Override
    @Transactional(readOnly = true)
    public ExplorerProgressDto getExplorerCourseProgress(String authorizationHeader, Long authenticatedPersonId, Long courseId) {
        ExplorersService.Explorer explorer = explorerService.findExplorerByPersonIdAndGroup_CourseId(
                authorizationHeader, authenticatedPersonId, courseId
        );

        CourseWithThemesProgressDto themesProgress = courseThemesProgressService.getThemesProgress(authorizationHeader, explorer);

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
