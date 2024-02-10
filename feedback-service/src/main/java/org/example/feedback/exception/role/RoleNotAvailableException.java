package org.example.feedback.exception.role;

public class RoleNotAvailableException extends RuntimeException {
    public RoleNotAvailableException() {
        super("Данная роль недоступна");
    }
}
