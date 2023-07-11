package org.example.exception.classes.requestEX;

public class RequestAlreadyClosedException extends RuntimeException {
    public RequestAlreadyClosedException() {
        super("Запрос уже был закрыт");
    }
}
