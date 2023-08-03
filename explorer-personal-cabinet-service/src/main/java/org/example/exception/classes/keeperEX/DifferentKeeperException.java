package org.example.exception.classes.keeperEX;

public class DifferentKeeperException extends RuntimeException {
    public DifferentKeeperException(Integer actualKeeperId, Integer requestedKeeperId) {
        super("Ваш хранитель: " + actualKeeperId + ", запрошенный хранитель: " + requestedKeeperId);
    }
}
