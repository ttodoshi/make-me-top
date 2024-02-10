package org.example.course.config.security;

import lombok.RequiredArgsConstructor;
import org.example.course.enums.AuthenticationRoleType;
import org.example.course.enums.CourseRoleType;
import org.example.course.exception.theme.CourseThemeNotFoundException;
import org.example.course.repository.CourseThemeRepository;
import org.example.course.service.ExplorerService;
import org.example.course.service.KeeperService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final CourseThemeRepository courseThemeRepository;

    private final ExplorerService explorerService;
    private final KeeperService keeperService;

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
                courseThemeRepository.findById(themeId)
                        .orElseThrow(() -> new CourseThemeNotFoundException(themeId))
                        .getCourseId(),
                role
        );
    }
}
