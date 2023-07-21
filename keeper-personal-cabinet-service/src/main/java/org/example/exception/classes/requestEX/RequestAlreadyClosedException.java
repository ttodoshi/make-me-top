package org.example.exception.classes.requestEX;

public class RequestAlreadyClosedException extends RuntimeException {
    public RequestAlreadyClosedException(Integer requestId) {
        super("Запрос " + requestId + " уже был закрыт");
    }
}
