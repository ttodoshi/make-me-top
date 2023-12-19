package org.example.person.config.security;

import lombok.RequiredArgsConstructor;
import org.example.person.enums.AuthenticationRoleType;
import org.example.person.enums.CourseRoleType;
import org.example.person.exception.classes.explorer.ExplorerNotFoundException;
import org.example.person.model.Explorer;
import org.example.person.model.Person;
import org.example.person.repository.ExplorerRepository;
import org.example.person.repository.KeeperRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;

    public boolean hasAnyAuthenticationRole(AuthenticationRoleType role) {
        for (GrantedAuthority authority : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
            if (authority.getAuthority().equals(role.name()))
                return true;
        }
        return false;
    }

    public boolean hasAnyCourseRole(Long courseId, CourseRoleType role) {
        Person person = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (role.equals(CourseRoleType.EXPLORER))
            return explorerRepository.findExplorerByPersonIdAndGroup_CourseId(person.getPersonId(), courseId).isPresent();
        else
            return keeperRepository.findKeeperByPersonIdAndCourseId(person.getPersonId(), courseId).isPresent();
    }

    public boolean isPersonExplorer(Long explorerId) {
        Person person = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Explorer explorer = explorerRepository.findById(explorerId)
                .orElseThrow(ExplorerNotFoundException::new);
        return explorer.getPersonId().equals(person.getPersonId());
    }

    public boolean isKeeperForExplorer(Long explorerId) {
        Person person = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Explorer explorer = explorerRepository.findById(explorerId)
                .orElseThrow(ExplorerNotFoundException::new);
        return explorer.getGroup().getKeeper().getPersonId().equals(person.getPersonId());
    }
}
