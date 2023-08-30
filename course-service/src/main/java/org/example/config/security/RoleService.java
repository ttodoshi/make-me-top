package org.example.config.security;

import org.example.model.role.AuthenticationRoleType;
import org.example.model.role.CourseRoleType;
import org.example.model.role.GeneralRoleType;

public interface RoleService {
    boolean hasAnyGeneralRole(GeneralRoleType role);

    boolean hasAnyAuthenticationRole(AuthenticationRoleType role);

    boolean hasAnyCourseRole(Integer courseId, CourseRoleType role);

    boolean hasAnyCourseRoleByThemeId(Integer themeId, CourseRoleType role);

    boolean hasAnyCourseRoleByHomeworkId(Integer homeworkId, CourseRoleType role);
}
