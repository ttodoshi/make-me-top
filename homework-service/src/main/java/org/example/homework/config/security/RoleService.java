package org.example.homework.config.security;

import lombok.RequiredArgsConstructor;
import org.example.grpc.ExplorerGroupsService;
import org.example.grpc.ExplorersService;
import org.example.grpc.PeopleService;
import org.example.homework.enums.AuthenticationRoleType;
import org.example.homework.enums.CourseRoleType;
import org.example.homework.exception.classes.explorer.ExplorerGroupNotFoundException;
import org.example.homework.exception.classes.homework.HomeworkNotFoundException;
import org.example.homework.exception.classes.homework.HomeworkRequestNotFound;
import org.example.homework.exception.classes.planet.PlanetNotFoundException;
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

    public boolean hasAnyCourseRole(Integer courseId, CourseRoleType role) {
        PeopleService.Person person = (PeopleService.Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (role.equals(CourseRoleType.EXPLORER))
            return explorerRepository.findExplorerByPersonIdAndGroup_CourseId(person.getPersonId(), courseId).isPresent();
        else
            return keeperRepository.findKeeperByPersonIdAndCourseId(person.getPersonId(), courseId).isPresent();
    }

    public boolean hasAnyCourseRoleByThemeId(Integer themeId, CourseRoleType role) {
        return hasAnyCourseRole(
                planetRepository.findById(themeId)
                        .orElseThrow(() -> new PlanetNotFoundException(themeId))
                        .getSystemId(),
                role
        );
    }

    public boolean hasAnyCourseRoleByHomeworkId(Integer homeworkId, CourseRoleType role) {
        return hasAnyCourseRoleByThemeId(
                homeworkRepository.findById(homeworkId)
                        .orElseThrow(() -> new HomeworkNotFoundException(homeworkId))
                        .getCourseThemeId(),
                role
        );
    }

    // TODO
    public boolean hasAnyCourseRoleByHomeworkIds(List<Integer> homeworkIds, CourseRoleType role) {
        return homeworkIds.stream()
                .allMatch(hId -> hasAnyCourseRoleByHomeworkId(hId, role));
    }

    public boolean hasAnyCourseRoleByHomeworkRequestId(Integer homeworkRequestId, CourseRoleType role) {
        return hasAnyCourseRoleByHomeworkId(
                homeworkRequestRepository.findById(homeworkRequestId).orElseThrow(
                        () -> new HomeworkRequestNotFound(homeworkRequestId)
                ).getHomeworkId(),
                role
        );
    }

    public boolean hasAnyCourseRoleByGroupId(Integer groupId, CourseRoleType role) {
        return hasAnyCourseRole(
                explorerGroupRepository.findById(groupId)
                        .orElseThrow(() -> new ExplorerGroupNotFoundException(groupId)).getCourseId(),
                role
        );
    }

    @Transactional(readOnly = true)
    public boolean hasAnyCourseRoleByExplorerIds(List<Integer> explorerIds, CourseRoleType role) {
        Map<Integer, ExplorersService.Explorer> explorers = explorerRepository.findExplorersByExplorerIdIn(explorerIds);
        Map<Integer, ExplorerGroupsService.ExplorerGroup> groups = explorerGroupRepository.findExplorerGroupsByGroupIdIn(
                explorers.values().stream().map(ExplorersService.Explorer::getGroupId).collect(Collectors.toList())
        );
        return groups
                .values()
                .stream()
                .map(ExplorerGroupsService.ExplorerGroup::getCourseId)
                .distinct()
                .allMatch(cId -> hasAnyCourseRole(cId, role)); // TODO
    }
}
