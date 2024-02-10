package org.example.person.exception.role;

public class RoleNotAvailableException extends RuntimeException {
    public RoleNotAvailableException() {
        super("Данная роль недоступна");
    }
}
