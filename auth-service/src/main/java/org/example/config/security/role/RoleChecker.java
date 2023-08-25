package org.example.config.security.role;

public interface RoleChecker {
    boolean isRoleAvailable(Integer personId);

    String getType();
}
