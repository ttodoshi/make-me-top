package org.example.exception.classes.keeperEX;

public class DifferentKeeperException extends RuntimeException {
    public DifferentKeeperException() {
        super("Этот запрос адресован не вам");
    }
}
