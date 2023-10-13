package org.example.exception.classes.keeperEX;

public class KeeperNotFoundException extends RuntimeException {
    public KeeperNotFoundException() {
        super("Хранитель не найден");
    }

    public KeeperNotFoundException(Integer keeperId) {
        super("Хранитель " + keeperId + " не найден");
    }
}
