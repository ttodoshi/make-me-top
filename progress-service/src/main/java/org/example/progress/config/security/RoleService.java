package org.example.progress.config.security;

import lombok.RequiredArgsConstructor;
import org.example.grpc.ExplorerGroupsService;
import org.example.grpc.ExplorersService;
import org.example.grpc.KeepersService;
import org.example.progress.enums.AuthenticationRoleType;
import org.example.progress.enums.CourseRoleType;
import org.example.progress.service.ExplorerGroupService;
import org.example.progress.service.ExplorerService;
import org.example.progress.service.KeeperService;
import org.example.progress.service.PlanetService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final KeeperService keeperService;
    private final ExplorerService explorerService;
    private final ExplorerGroupService explorerGroupService;
    private final PlanetService planetService;

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

    public boolean hasAnyCourseRoleByThemeId(String authorizationHeader, Long authenticatedPersonId, Long themeId, CourseRoleType role) {
        return hasAnyCourseRole(
                authorizationHeader, authenticatedPersonId,
                planetService.findById(authorizationHeader, themeId).getSystemId(),
                role
        );
    }

    public boolean isExplorersKeeper(String authorizationHeader, Long authenticatedPersonId, List<Long> explorerIds) {
        List<Long> groupIds = explorerService.findExplorersByExplorerIdIn(authorizationHeader, explorerIds)
                .values()
                .stream()
                .map(ExplorersService.Explorer::getGroupId)
                .collect(Collectors.toList());
        List<Long> groupsKeeperIds = explorerGroupService
                .findExplorerGroupsByGroupIdIn(authorizationHeader, groupIds)
                .values()
                .stream()
                .map(ExplorerGroupsService.ExplorerGroup::getKeeperId)
                .collect(Collectors.toList());
        List<KeepersService.Keeper> personKeepers = keeperService.findKeepersByPersonId(authenticatedPersonId);
        return personKeepers.stream()
                .map(KeepersService.Keeper::getKeeperId)
                .collect(Collectors.toSet())
                .containsAll(groupsKeeperIds);
    }
}
