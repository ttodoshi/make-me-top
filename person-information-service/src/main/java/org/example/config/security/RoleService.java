package org.example.config.security;

import org.example.model.role.AuthenticationRoleType;

public interface RoleService {
    boolean hasAnyAuthenticationRole(AuthenticationRoleType role);
}
