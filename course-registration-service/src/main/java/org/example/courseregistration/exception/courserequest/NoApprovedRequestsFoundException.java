package org.example.courseregistration.exception.courserequest;

public class NoApprovedRequestsFoundException extends RuntimeException {
    public NoApprovedRequestsFoundException() {
        super("Не найдено одобренных запросов, чтобы можно было начать обучение");
    }
}
