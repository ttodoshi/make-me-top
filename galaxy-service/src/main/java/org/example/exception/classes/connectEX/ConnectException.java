package org.example.exception.classes.connectEX;

public class ConnectException extends RuntimeException {
    public ConnectException(Throwable cause) {
        super("Бортовой компьютер не смог связать с внутренней системой данных", cause);
    }
}
