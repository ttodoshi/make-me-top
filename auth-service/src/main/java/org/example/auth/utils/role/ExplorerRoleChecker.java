package org.example.auth.utils.role;

import org.example.auth.enums.AuthenticationRoleType;
import org.springframework.stereotype.Component;

@Component
public class ExplorerRoleChecker implements RoleChecker {
    @Override
    public boolean isRoleAvailable(Long personId, String mmtrUserToken) {
        return true;
    }

    @Override
    public String getType() {
        return AuthenticationRoleType.EXPLORER.name();
    }
}
