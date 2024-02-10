package org.example.courseregistration.exception.courserequest;

public class RequestAlreadyClosedException extends RuntimeException {
    public RequestAlreadyClosedException(Long requestId) {
        super("Запрос " + requestId + " уже был закрыт");
    }
}

