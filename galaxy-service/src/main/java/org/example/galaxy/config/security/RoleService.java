package org.example.galaxy.config.security;

import org.example.galaxy.enums.AuthenticationRoleType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class RoleService {
    public boolean hasAnyAuthenticationRole(Collection<? extends GrantedAuthority> authorities, AuthenticationRoleType role) {
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals(role.name()))
                return true;
        }
        return false;
    }
}
