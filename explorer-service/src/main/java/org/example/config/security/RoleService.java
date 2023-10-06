package org.example.config.security;

import lombok.RequiredArgsConstructor;
import org.example.dto.person.PersonDto;
import org.example.config.security.role.AuthenticationRoleType;
import org.example.config.security.role.CourseRoleType;
import org.example.exception.classes.explorerEX.ExplorerGroupNotFoundException;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.model.Explorer;
import org.example.repository.ExplorerGroupRepository;
import org.example.repository.ExplorerRepository;
import org.example.repository.KeeperRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;
    private final ExplorerGroupRepository explorerGroupRepository;

    public boolean hasAnyAuthenticationRole(AuthenticationRoleType role) {
        for (GrantedAuthority authority : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
            if (authority.getAuthority().equals(role.name()))
                return true;
        }
        return false;
    }

    public boolean hasAnyCourseRole(Integer courseId, CourseRoleType role) {
        PersonDto person = (PersonDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (role.equals(CourseRoleType.EXPLORER))
            return explorerRepository.findExplorerByPersonIdAndGroup_CourseId(person.getPersonId(), courseId).isPresent();
        else
            return keeperRepository.findKeeperByPersonIdAndCourseId(person.getPersonId(), courseId).isPresent();
    }

    public boolean hasAnyCourseRoleByGroupId(Integer groupId, CourseRoleType role) {
        return hasAnyCourseRole(
                explorerGroupRepository.findById(groupId)
                        .orElseThrow(() -> new ExplorerGroupNotFoundException(groupId))
                        .getCourseId(),
                role
        );
    }

    public boolean isPersonExplorer(Integer explorerId) {
        PersonDto person = (PersonDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Explorer explorer = explorerRepository.findById(explorerId)
                .orElseThrow(ExplorerNotFoundException::new);
        return explorer.getPersonId().equals(person.getPersonId());
    }
}
