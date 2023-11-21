package org.example.auth.exception.classes.token;

public class FailedRefreshException extends RuntimeException {
    public FailedRefreshException() {
        super("Не удалось выполнить refresh-запрос");
    }
}
