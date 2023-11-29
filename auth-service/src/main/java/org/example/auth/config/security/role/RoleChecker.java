package org.example.auth.config.security.role;

public interface RoleChecker {
    boolean isRoleAvailable(Long personId);

    String getType();
}
