package org.example.exception.classes.connectEX;

public class ConnectException extends RuntimeException {
    public ConnectException() {
        super("Бортовой компьютер не смог связаться с внутренней системой данных");
    }
}
