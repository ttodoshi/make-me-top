package org.example.exception.classes.requestEX;

public class NoApprovedRequestsFoundException extends RuntimeException {
    public NoApprovedRequestsFoundException() {
        super("Не найдено одобренных запросов, чтобы можно было начать обучение");
    }
}
