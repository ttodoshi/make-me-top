package org.example.person.config.security;

import lombok.RequiredArgsConstructor;
import org.example.person.enums.AuthenticationRoleType;
import org.example.person.enums.CourseRoleType;
import org.example.person.exception.explorer.ExplorerNotFoundException;
import org.example.person.model.Explorer;
import org.example.person.repository.ExplorerRepository;
import org.example.person.repository.KeeperRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;

    public boolean hasAnyAuthenticationRole(Collection<? extends GrantedAuthority> authorities, AuthenticationRoleType role) {
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals(role.name()))
                return true;
        }
        return false;
    }

    public boolean hasAnyCourseRole(Long authenticatedPersonId, Long courseId, CourseRoleType role) {
        if (role.equals(CourseRoleType.EXPLORER))
            return explorerRepository.findExplorerByPersonIdAndGroup_CourseId(authenticatedPersonId, courseId).isPresent();
        else
            return keeperRepository.findKeeperByPersonIdAndCourseId(authenticatedPersonId, courseId).isPresent();
    }

    public boolean isPersonExplorer(Long authenticatedPersonId, Long explorerId) {
        Explorer explorer = explorerRepository.findById(explorerId)
                .orElseThrow(ExplorerNotFoundException::new);
        return explorer.getPersonId().equals(authenticatedPersonId);
    }

    public boolean isKeeperForExplorer(Long authenticatedPersonId, Long explorerId) {
        Explorer explorer = explorerRepository.findById(explorerId)
                .orElseThrow(ExplorerNotFoundException::new);
        return explorer.getGroup().getKeeper().getPersonId().equals(authenticatedPersonId);
    }
}
