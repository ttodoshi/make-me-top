package org.example.person.exception.keeper;

public class KeeperNotFoundException extends RuntimeException {
    public KeeperNotFoundException() {
        super("Хранитель не найден");
    }

    public KeeperNotFoundException(Long keeperId) {
        super("Хранитель " + keeperId + " не найден");
    }
}
