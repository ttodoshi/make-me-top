package org.example.exception.classes.connectEX;

public class ConnectException extends RuntimeException {
    public ConnectException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
