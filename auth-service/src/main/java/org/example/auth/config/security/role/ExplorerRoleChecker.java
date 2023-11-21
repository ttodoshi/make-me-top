package org.example.auth.config.security.role;

import org.example.auth.enums.AuthenticationRoleType;
import org.springframework.stereotype.Component;

@Component
public class ExplorerRoleChecker implements RoleChecker {
    @Override
    public boolean isRoleAvailable(Integer personId) {
        return true;
    }

    @Override
    public String getType() {
        return AuthenticationRoleType.EXPLORER.name();
    }
}
