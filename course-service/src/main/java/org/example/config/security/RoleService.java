package org.example.config.security;

import org.example.config.security.role.AuthenticationRoleType;
import org.example.config.security.role.CourseRoleType;

import java.util.List;

public interface RoleService {
    boolean hasAnyAuthenticationRole(AuthenticationRoleType role);

    boolean hasAnyCourseRole(Integer courseId, CourseRoleType role);

    boolean hasAnyCourseRoleByThemeId(Integer themeId, CourseRoleType role);

    boolean hasAnyCourseRoleByThemeIds(List<Integer> themeIds, CourseRoleType role);
}
