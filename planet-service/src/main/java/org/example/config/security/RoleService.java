package org.example.config.security;

import lombok.RequiredArgsConstructor;
import org.example.model.GeneralRoleType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("RoleService")
@RequiredArgsConstructor
public class RoleService {

    public boolean hasAnyGeneralRole(GeneralRoleType role) {
        for (GrantedAuthority authority : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
            if (authority.getAuthority().equals(role.name()))
                return true;
        }
        return false;
    }
}
