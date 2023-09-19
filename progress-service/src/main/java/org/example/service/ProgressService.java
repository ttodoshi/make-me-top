package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.PersonDto;
import org.example.dto.explorer.ExplorerDto;
import org.example.dto.planet.PlanetDto;
import org.example.dto.progress.CourseWithProgressDto;
import org.example.dto.progress.CourseWithThemesProgressDto;
import org.example.dto.progress.CoursesStateDto;
import org.example.dto.starsystem.GetStarSystemWithDependenciesDto;
import org.example.dto.starsystem.StarSystemDto;
import org.example.dto.starsystem.SystemDependencyModelDto;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService {
    private final CourseMarkRepository courseMarkRepository;
    private final CourseThemeCompletionRepository courseThemeCompletionRepository;
    private final ExplorerRepository explorerRepository;
    private final ExplorerGroupRepository explorerGroupRepository;
    private final PlanetRepository planetRepository;
    private final StarSystemRepository starSystemRepository;

    private final CourseThemesProgressService courseThemesProgressService;
    private final PersonService personService;

    @Transactional(readOnly = true)
    public CoursesStateDto getCoursesProgressForCurrentUser(Integer galaxyId) {
        PersonDto authenticatedPerson = personService.getAuthenticatedPerson();
        Set<Integer> openedCourses = new LinkedHashSet<>();
        Set<CourseWithProgressDto> studiedCourses = new LinkedHashSet<>();
        Set<Integer> closedCourses = new LinkedHashSet<>();
        for (StarSystemDto system : starSystemRepository.getSystemsByGalaxyId(galaxyId)) {
            Optional<ExplorerDto> explorerOptional = explorerRepository
                    .findExplorerByPersonIdAndGroup_CourseId(authenticatedPerson.getPersonId(), system.getSystemId());
            if (explorerOptional.isPresent()) {
                ExplorerDto explorer = explorerOptional.get();
                studiedCourses.add(
                        new CourseWithProgressDto(
                                system.getSystemId(),
                                getCourseProgress(explorer.getExplorerId(), system.getSystemId())
                        )
                );
            } else if (hasUncompletedParents(authenticatedPerson.getPersonId(), system.getSystemId())) {
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

    public CourseWithThemesProgressDto getExplorerThemesProgress(Integer explorerId) {
        ExplorerDto explorer = explorerRepository.findById(explorerId)
                .orElseThrow(() -> new ExplorerNotFoundException(explorerId));
        return courseThemesProgressService.getThemesProgress(explorer);
    }

    private boolean hasUncompletedParents(Integer personId, Integer systemId) {
        GetStarSystemWithDependenciesDto systemWithDependencies = starSystemRepository
                .getStarSystemWithDependencies(systemId);
        for (SystemDependencyModelDto system : getParentDependencies(systemWithDependencies)) {
            Optional<ExplorerDto> explorer = explorerRepository.findExplorerByPersonIdAndGroup_CourseId(personId, system.getSystemId());
            if (explorer.isEmpty() || getCourseProgress(explorer.get().getExplorerId(), system.getSystemId()) < 100) {
                return true;
            } else if (system.getIsAlternative())
                return false;
        }
        return false;
    }

    private double getCourseProgress(Integer explorerId, Integer systemId) {
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
    public List<Integer> getExplorerIdsNeededFinalAssessment(List<Integer> explorerIds) {
        List<Integer> explorersWithFinalAssessment = getExplorerIdsWithFinalAssessment(explorerIds);
        Map<Integer, ExplorerDto> explorers = explorerRepository.findExplorersByExplorerIdIn(explorerIds);
        Map<Integer, Integer> explorerGroupCourseIds = explorerGroupRepository
                .findExplorerGroupsCourseIdByGroupIdIn(
                        explorers.values().stream().map(ExplorerDto::getGroupId).collect(Collectors.toList())
                );
        Map<Integer, List<PlanetDto>> planets = planetRepository.findPlanetsBySystemIdIn(
                explorerGroupCourseIds.values().stream().distinct().collect(Collectors.toList())
        );
        return explorerIds.stream()
                .filter(eId -> !explorersWithFinalAssessment.contains(eId))
                .filter(eId -> {
                    Integer explorerCourseId = explorerGroupCourseIds.get(
                            explorers.get(eId).getGroupId()
                    );
                    return courseThemeCompletionRepository
                            .getCourseProgress(
                                    eId,
                                    planets.get(explorerCourseId).size()
                            ) == 100;
                })
                .collect(Collectors.toList());
    }

    public List<Integer> getExplorerIdsWithFinalAssessment(List<Integer> explorerIds) {
        return courseMarkRepository.findExplorerIdsWithFinalAssessment(explorerIds);
    }
}
