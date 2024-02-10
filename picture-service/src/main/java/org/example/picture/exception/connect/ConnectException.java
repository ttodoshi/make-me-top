package org.example.picture.exception.connect;

public class ConnectException extends RuntimeException {
    public ConnectException() {
        super("Бортовой компьютер не смог связаться с внутренней системой данных");
    }

    public ConnectException(Throwable cause) {
        super("Бортовой компьютер не смог связаться с внутренней системой данных", cause);
    }
}