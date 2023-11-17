package org.example.config.security;

import org.example.config.security.role.AuthenticationRoleType;
import org.example.config.security.role.CourseRoleType;

public interface RoleService {
    boolean hasAnyAuthenticationRole(AuthenticationRoleType role);

    boolean hasAnyCourseRole(Integer courseId, CourseRoleType role);

    boolean hasAnyCourseRoleByThemeId(Integer themeId, CourseRoleType role);
}
