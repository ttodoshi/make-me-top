package org.example.courseregistration.exception.classes.request;

public class RequestAlreadyClosedException extends RuntimeException {
    public RequestAlreadyClosedException(Long requestId) {
        super("Запрос " + requestId + " уже был закрыт");
    }
}

