package org.example.feedback.exception.classes.role;

public class RoleNotAvailableException extends RuntimeException {
    public RoleNotAvailableException() {
        super("Данная роль недоступна");
    }
}
