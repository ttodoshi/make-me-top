package org.example.courseregistration.exception.courserequest;

public class StatusNotFoundException extends RuntimeException {
    public StatusNotFoundException(Object status) {
        super("Статус запроса " + status + " не обнаружен");
    }
}
