package org.example.exception.classes.keeperEX;

public class DifferentKeeperException extends RuntimeException {
    public DifferentKeeperException() {
        super("У вас нет прав, чтобы сделать это"); // Этот запрос адресован не вам
    }
}
