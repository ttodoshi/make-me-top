package org.example.courseregistration.exception.classes.request;

public class NoApprovedRequestsFoundException extends RuntimeException {
    public NoApprovedRequestsFoundException() {
        super("Не найдено одобренных запросов, чтобы можно было начать обучение");
    }
}
