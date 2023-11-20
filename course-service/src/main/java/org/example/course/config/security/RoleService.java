package org.example.course.config.security;

import lombok.RequiredArgsConstructor;
import org.example.course.enums.AuthenticationRoleType;
import org.example.course.enums.CourseRoleType;
import org.example.course.exception.classes.theme.CourseThemeNotFoundException;
import org.example.grpc.PeopleService;
import org.example.course.repository.CourseThemeRepository;
import org.example.course.repository.ExplorerRepository;
import org.example.course.repository.KeeperRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;
    private final CourseThemeRepository courseThemeRepository;

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
                courseThemeRepository.findById(themeId)
                        .orElseThrow(() -> new CourseThemeNotFoundException(themeId))
                        .getCourseId(),
                role
        );
    }
}
