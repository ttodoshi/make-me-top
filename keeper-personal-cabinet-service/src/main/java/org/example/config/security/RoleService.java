package org.example.config.security;

import lombok.RequiredArgsConstructor;
import org.example.model.AuthenticationRoleType;
import org.example.model.CourseRoleType;
import org.example.model.GeneralRoleType;
import org.example.model.Person;
import org.example.repository.ExplorerRepository;
import org.example.repository.KeeperRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("RoleService")
@RequiredArgsConstructor
public class RoleService {
    private final KeeperRepository keeperRepository;
    private final ExplorerRepository explorerRepository;

    public boolean hasAnyAuthenticationRole(AuthenticationRoleType role) {
        for (GrantedAuthority authority : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
            if (authority.getAuthority().equals(role.name()))
                return true;
        }
        return false;
    }

    public boolean hasAnyGeneralRole(GeneralRoleType role) {
        for (GrantedAuthority authority : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
            if (authority.getAuthority().equals(role.name()))
                return true;
        }
        return false;
    }

    public boolean hasAnyCourseRole(Integer courseId, CourseRoleType role) {
        Person person = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (role.equals(CourseRoleType.EXPLORER))
            return explorerRepository.findExplorerByPersonIdAndCourseId(person.getPersonId(), courseId).isPresent();
        else
            return keeperRepository.findKeeperByPersonIdAndCourseId(person.getPersonId(), courseId).isPresent();
    }
}
