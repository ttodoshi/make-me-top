package org.example.exception.classes.tokenEX;

public class FailedRefreshException extends RuntimeException {
    public FailedRefreshException() {
        super("Не удалось выполнить refresh-запрос");
    }
}
