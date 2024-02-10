package org.example.auth.exception.token;

public class FailedLogoutException extends RuntimeException {
    public FailedLogoutException() {
        super("Не удалось выполнить logout-запрос");
    }
}
