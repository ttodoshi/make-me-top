package org.example.homework.config.security;

import lombok.RequiredArgsConstructor;
import org.example.grpc.ExplorerGroupsService;
import org.example.grpc.ExplorersService;
import org.example.grpc.PeopleService;
import org.example.homework.dto.planet.PlanetDto;
import org.example.homework.enums.AuthenticationRoleType;
import org.example.homework.enums.CourseRoleType;
import org.example.homework.exception.classes.explorer.ExplorerGroupNotFoundException;
import org.example.homework.exception.classes.homework.HomeworkNotFoundException;
import org.example.homework.exception.classes.homework.HomeworkRequestNotFound;
import org.example.homework.exception.classes.planet.PlanetNotFoundException;
import org.example.homework.model.Homework;
import org.example.homework.repository.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final KeeperRepository keeperRepository;
    private final ExplorerRepository explorerRepository;
    private final ExplorerGroupRepository explorerGroupRepository;
    private final PlanetRepository planetRepository;
    private final HomeworkRepository homeworkRepository;
    private final HomeworkRequestRepository homeworkRequestRepository;

    public boolean hasAnyAuthenticationRole(AuthenticationRoleType role) {
        for (GrantedAuthority authority : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
            if (authority.getAuthority().equals(role.name()))
                return true;
        }
        return false;
    }

    public boolean hasAnyCourseRole(Long courseId, CourseRoleType role) {
        PeopleService.Person person = (PeopleService.Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (role.equals(CourseRoleType.EXPLORER))
            return explorerRepository.findExplorerByPersonIdAndGroup_CourseId(person.getPersonId(), courseId).isPresent();
        else
            return keeperRepository.findKeeperByPersonIdAndCourseId(person.getPersonId(), courseId).isPresent();
    }

    public boolean hasAnyCoursesRole(List<Long> courseIds, CourseRoleType role) {
        PeopleService.Person person = (PeopleService.Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (role.equals(CourseRoleType.EXPLORER)) {
            return courseIds.stream().allMatch(cId ->
                    explorerRepository.findExplorersByPersonIdAndGroupCourseIdIn(person.getPersonId(), courseIds)
                            .getExplorerWithCourseIdMapMap()
                            .containsKey(cId));
        } else {
            return courseIds.stream().allMatch(cId ->
                    keeperRepository.findKeepersByPersonIdAndGroupCourseIdIn(person.getPersonId(), courseIds)
                            .getKeeperWithCourseIdMapMap()
                            .containsKey(cId));
        }
    }

    public boolean hasAnyCourseRoleByThemeId(Long themeId, CourseRoleType role) {
        return hasAnyCourseRole(
                planetRepository.findById(themeId)
                        .orElseThrow(() -> new PlanetNotFoundException(themeId))
                        .getSystemId(),
                role
        );
    }

    public boolean hasAnyCourseRoleByHomeworkId(Long homeworkId, CourseRoleType role) {
        return hasAnyCourseRoleByThemeId(
                homeworkRepository.findById(homeworkId)
                        .orElseThrow(() -> new HomeworkNotFoundException(homeworkId))
                        .getCourseThemeId(),
                role
        );
    }

    public boolean hasAnyCourseRoleByHomeworkIds(List<Long> homeworkIds, CourseRoleType role) {
        Map<Long, PlanetDto> planets = planetRepository.findPlanetsByPlanetIdIn(
                homeworkRepository.findAllByHomeworkIdIn(homeworkIds)
                        .stream()
                        .map(Homework::getCourseThemeId)
                        .collect(Collectors.toList())
        );
        return hasAnyCoursesRole(
                planets.values()
                        .stream()
                        .map(PlanetDto::getSystemId)
                        .distinct()
                        .collect(Collectors.toList()),
                role
        );
    }

    public boolean hasAnyCourseRoleByHomeworkRequestId(Long homeworkRequestId, CourseRoleType role) {
        return hasAnyCourseRoleByHomeworkId(
                homeworkRequestRepository.findById(homeworkRequestId).orElseThrow(
                        () -> new HomeworkRequestNotFound(homeworkRequestId)
                ).getHomeworkId(),
                role
        );
    }

    public boolean hasAnyCourseRoleByGroupId(Long groupId, CourseRoleType role) {
        return hasAnyCourseRole(
                explorerGroupRepository.findById(groupId)
                        .orElseThrow(() -> new ExplorerGroupNotFoundException(groupId)).getCourseId(),
                role
        );
    }

    @Transactional(readOnly = true)
    public boolean hasAnyCourseRoleByExplorerIds(List<Long> explorerIds, CourseRoleType role) {
        Map<Long, ExplorersService.Explorer> explorers = explorerRepository.findExplorersByExplorerIdIn(explorerIds);
        Map<Long, ExplorerGroupsService.ExplorerGroup> groups = explorerGroupRepository.findExplorerGroupsByGroupIdIn(
                explorers.values().stream().map(ExplorersService.Explorer::getGroupId).collect(Collectors.toList())
        );
        return hasAnyCoursesRole(
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
