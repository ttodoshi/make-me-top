package org.example.exception.classes.user;

public class RoleNotAvailableException extends RuntimeException {
    public RoleNotAvailableException() {
        super("Данная роль недоступна");
    }
}
