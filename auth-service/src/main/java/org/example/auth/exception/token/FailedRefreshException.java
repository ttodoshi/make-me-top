package org.example.auth.exception.token;

public class FailedRefreshException extends RuntimeException {
    public FailedRefreshException() {
        super("Не удалось выполнить refresh-запрос");
    }

    public FailedRefreshException(Throwable cause) {
        super("Не удалось выполнить refresh-запрос", cause);
    }
}
