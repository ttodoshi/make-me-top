package org.example.config.security;

import lombok.RequiredArgsConstructor;
import org.example.model.Person;
import org.example.model.role.AuthenticationRoleType;
import org.example.model.role.CourseRoleType;
import org.example.repository.ExplorerRepository;
import org.example.repository.KeeperRepository;
import org.example.repository.course.CourseRepository;
import org.example.repository.course.CourseThemeRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final KeeperRepository keeperRepository;
    private final ExplorerRepository explorerRepository;
    private final CourseRepository courseRepository;
    private final CourseThemeRepository courseThemeRepository;

    public boolean hasAnyAuthenticationRole(AuthenticationRoleType role) {
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

    public boolean hasAnyCourseRoleByThemeId(Integer themeId, CourseRoleType role) {
        return hasAnyCourseRole(
                courseRepository.getCourseIdByThemeId(themeId),
                role
        );
    }
}
