package org.example.auth.utils.role;

public interface RoleChecker {
    boolean isRoleAvailable(Long personId, String mmtrUserToken);

    String getType();
}
