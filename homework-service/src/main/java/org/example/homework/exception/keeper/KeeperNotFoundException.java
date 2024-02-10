package org.example.homework.exception.keeper;

public class KeeperNotFoundException extends RuntimeException {
    public KeeperNotFoundException() {
        super("Хранитель не найден");
    }
}
