package org.example.galaxy.exception.connect;

public class ConnectException extends RuntimeException {
    public ConnectException(Throwable cause) {
        super("Бортовой компьютер не смог связаться с внутренней системой данных", cause);
    }
}