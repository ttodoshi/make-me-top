package org.example.auth.exception.person;

public class RoleNotAvailableException extends RuntimeException {
    public RoleNotAvailableException() {
        super("Данная роль недоступна");
    }
}
