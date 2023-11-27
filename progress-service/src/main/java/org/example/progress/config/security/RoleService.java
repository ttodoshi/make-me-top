package org.example.progress.config.security;

import lombok.RequiredArgsConstructor;
import org.example.grpc.ExplorerGroupsService;
import org.example.grpc.ExplorersService;
import org.example.grpc.KeepersService;
import org.example.grpc.PeopleService;
import org.example.progress.enums.AuthenticationRoleType;
import org.example.progress.enums.CourseRoleType;
import org.example.progress.exception.classes.explorer.ExplorerNotFoundException;
import org.example.progress.exception.classes.planet.PlanetNotFoundException;
import org.example.progress.repository.ExplorerGroupRepository;
import org.example.progress.repository.ExplorerRepository;
import org.example.progress.repository.KeeperRepository;
import org.example.progress.repository.PlanetRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final KeeperRepository keeperRepository;
    private final ExplorerRepository explorerRepository;
    private final ExplorerGroupRepository explorerGroupRepository;
    private final PlanetRepository planetRepository;

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

    @Transactional(readOnly = true)
    public boolean hasAnyCourseRoleByExplorerId(Integer explorerId, CourseRoleType role) {
        return hasAnyCourseRole(
                explorerGroupRepository.getReferenceById(
                                explorerRepository.findById(explorerId)
                                        .orElseThrow(() -> new ExplorerNotFoundException(explorerId))
                                        .getGroupId()
                        )
                        .getCourseId(),
                role
        );
    }

    public boolean hasAnyCourseRoleByThemeId(Integer themeId, CourseRoleType role) {
        return hasAnyCourseRole(
                planetRepository.findById(themeId)
                        .orElseThrow(() -> new PlanetNotFoundException(themeId))
                        .getSystemId(),
                role
        );
    }

    public boolean isExplorersKeeper(List<Integer> explorerIds) {
        PeopleService.Person person = (PeopleService.Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Integer> groupIds = explorerRepository.findExplorersByExplorerIdIn(explorerIds)
                .values()
                .stream()
                .map(ExplorersService.Explorer::getGroupId)
                .collect(Collectors.toList());
        List<Integer> groupsKeeperIds = explorerGroupRepository
                .findExplorerGroupsByGroupIdIn(groupIds)
                .values()
                .stream()
                .map(ExplorerGroupsService.ExplorerGroup::getKeeperId)
                .collect(Collectors.toList());
        List<KeepersService.Keeper> personKeepers = keeperRepository.findKeepersByPersonId(
                person.getPersonId()
        );
        return personKeepers.stream()
                .map(KeepersService.Keeper::getKeeperId)
                .collect(Collectors.toSet())
                .containsAll(
                        groupsKeeperIds
                );
    }
}
