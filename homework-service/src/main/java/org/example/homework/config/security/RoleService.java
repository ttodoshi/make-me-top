package org.example.homework.config.security;

import lombok.RequiredArgsConstructor;
import org.example.grpc.ExplorerGroupsService;
import org.example.grpc.ExplorersService;
import org.example.homework.dto.planet.PlanetDto;
import org.example.homework.enums.AuthenticationRoleType;
import org.example.homework.enums.CourseRoleType;
import org.example.homework.exception.homework.HomeworkNotFoundException;
import org.example.homework.model.Homework;
import org.example.homework.repository.HomeworkRepository;
import org.example.homework.service.ExplorerGroupService;
import org.example.homework.service.ExplorerService;
import org.example.homework.service.KeeperService;
import org.example.homework.service.PlanetService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final KeeperService keeperService;
    private final ExplorerService explorerService;
    private final ExplorerGroupService explorerGroupService;
    private final PlanetService planetService;
    private final HomeworkRepository homeworkRepository;

    public boolean hasAnyAuthenticationRole(Collection<? extends GrantedAuthority> authorities, AuthenticationRoleType role) {
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals(role.name()))
                return true;
        }
        return false;
    }

    public boolean hasAnyCourseRole(String authorizationHeader, Long authenticatedPersonId, Long courseId, CourseRoleType role) {
        if (role.equals(CourseRoleType.EXPLORER))
            return explorerService.existsExplorerByPersonIdAndGroup_CourseId(authorizationHeader, authenticatedPersonId, courseId);
        else
            return keeperService.existsKeeperByPersonIdAndCourseId(authorizationHeader, authenticatedPersonId, courseId);
    }

    public boolean hasAnyCoursesRole(String authorizationHeader, Long authenticatedPersonId, List<Long> courseIds, CourseRoleType role) {
        if (role.equals(CourseRoleType.EXPLORER)) {
            return courseIds.stream().allMatch(cId ->
                    explorerService.findExplorersByPersonIdAndGroupCourseIdIn(
                                    authorizationHeader, authenticatedPersonId, courseIds
                            ).getExplorerWithCourseIdMapMap()
                            .containsKey(cId));
        } else {
            return courseIds.stream().allMatch(cId ->
                    keeperService.findKeepersByPersonIdAndGroupCourseIdIn(
                                    authorizationHeader, authenticatedPersonId, courseIds
                            ).getKeeperWithCourseIdMapMap()
                            .containsKey(cId));
        }
    }

    public boolean hasAnyCourseRoleByThemeId(String authorizationHeader, Long authenticatedPersonId, Long themeId, CourseRoleType role) {
        return hasAnyCourseRole(
                authorizationHeader,
                authenticatedPersonId,
                planetService.findById(authorizationHeader, themeId).getSystemId(),
                role
        );
    }

    public boolean hasAnyCourseRoleByHomeworkId(String authorizationHeader, Long authenticatedPersonId, Long homeworkId, CourseRoleType role) {
        return hasAnyCourseRoleByThemeId(
                authorizationHeader,
                authenticatedPersonId,
                homeworkRepository.findById(homeworkId)
                        .orElseThrow(() -> new HomeworkNotFoundException(homeworkId))
                        .getCourseThemeId(),
                role
        );
    }

    public boolean hasAnyCourseRoleByHomeworkIds(String authorizationHeader, Long authenticatedPersonId, List<Long> homeworkIds, CourseRoleType role) {
        Map<Long, PlanetDto> planets = planetService.findPlanetsByPlanetIdIn(
                authorizationHeader,
                homeworkRepository.findAllByHomeworkIdIn(homeworkIds)
                        .stream()
                        .map(Homework::getCourseThemeId)
                        .collect(Collectors.toList())
        );
        return hasAnyCoursesRole(
                authorizationHeader,
                authenticatedPersonId,
                planets.values()
                        .stream()
                        .map(PlanetDto::getSystemId)
                        .distinct()
                        .collect(Collectors.toList()),
                role
        );
    }

    @Transactional(readOnly = true)
    public boolean hasAnyCourseRoleByExplorerIds(String authorizationHeader, Long authenticatedPersonId, List<Long> explorerIds, CourseRoleType role) {
        Map<Long, ExplorersService.Explorer> explorers = explorerService.findExplorersByExplorerIdIn(authorizationHeader, explorerIds);
        Map<Long, ExplorerGroupsService.ExplorerGroup> groups = explorerGroupService.findExplorerGroupsByGroupIdIn(
                authorizationHeader,
                explorers.values().stream().map(ExplorersService.Explorer::getGroupId).collect(Collectors.toList())
        );
        return hasAnyCoursesRole(
                authorizationHeader,
                authenticatedPersonId,
                groups
                        .values()
                        .stream()
                        .map(ExplorerGroupsService.ExplorerGroup::getCourseId)
                        .distinct()
                        .collect(Collectors.toList()),
                role
        );
    }
}
